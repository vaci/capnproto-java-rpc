package org.capnproto;

import java.util.HashMap;
import java.util.Map;

public class RpcDumper {

    private final Map<Long, Schema.Node.Reader> schemas = new HashMap<>();
    private final Map<Integer, Long> returnTypes = new HashMap<>();

    void addSchema(long schemaId, Schema.Node.Reader node) {
        this.schemas.put(schemaId, node);
    }

    private void setReturnType(int schemaId, long schema) {
        this.returnTypes.put(schemaId, schema);
    }

    private Long getReturnType(int schemaId) {
        return this.returnTypes.get(schemaId);
    }

    private String renderDynamic(Schema.Node.Const.Reader constNode) {
        var text = new StringBuilder();
        var value = constNode.getValue();
        var valueText = switch (value.which()) {
            case VOID -> "null";
            case BOOL -> String.valueOf(value.getBool());
            case INT8 -> String.valueOf(value.getInt8());
            case INT16 -> String.valueOf(value.getInt16());
            case INT32 -> String.valueOf(value.getInt32());
            case INT64 -> String.valueOf(value.getInt64());
            case UINT8 -> String.valueOf(value.getUint8());
            case UINT16 -> String.valueOf(value.getUint16());
            case UINT32 -> String.valueOf(value.getUint32());
            case UINT64 -> String.valueOf(value.getUint64());
            case FLOAT32 -> String.valueOf(value.getFloat32());
            case FLOAT64 -> String.valueOf(value.getFloat64());
            case TEXT -> value.getText().toString();
            case DATA -> "<"  + value.getData().size() + " opaque bytes>";
            case LIST -> "List";
            case ENUM -> String.valueOf(value.getEnum());
            case STRUCT -> "";
            case INTERFACE -> "";
            case ANY_POINTER -> "any";
            case _NOT_IN_SCHEMA -> "<unknown>";
        };
        text.append(valueText);
        return text.toString();
    }

    private String renderDynamic(Schema.Node.Struct.Reader structNode) {
        var text = new StringBuilder();
        text.append("{");
        if (structNode.hasFields()) {
            for (var field: structNode.getFields()) {
                if (field.hasName()) {
                    text.append(field.getName().toString());
                }
                text.append("@");
                var ordinal = field.getOrdinal();
                text.append(switch (ordinal.which()) {
                    case EXPLICIT -> String.valueOf(ordinal.getExplicit());
                    case IMPLICIT -> String.valueOf(ordinal.getImplicit());
                    case _NOT_IN_SCHEMA -> "?";
                });
                text.append(",");
            }
        }
        text.append("}");
        return text.toString();
    }

    private String renderDynamic(long id, AnyPointer.Reader any) {
        var node = this.schemas.get(id);
        if (node == null) {
            return "<unknown schema>";
        }

        return switch (node.which()) {
            case FILE -> "";
            case STRUCT -> renderDynamic(node.getStruct());
            case ENUM, INTERFACE -> node.getDisplayName().toString();
            case CONST -> renderDynamic(node.getConst());
            case ANNOTATION -> "";
            case _NOT_IN_SCHEMA -> "";
        };
    }

    private String dumpCap(RpcProtocol.CapDescriptor.Reader cap) {
        var capText = switch (cap.which()) {
            case NONE -> "null";
            case SENDER_HOSTED -> cap.getSenderHosted();
            case SENDER_PROMISE -> cap.getSenderPromise();
            case RECEIVER_HOSTED -> cap.getReceiverHosted();
            case RECEIVER_ANSWER -> cap.getReceiverAnswer().getQuestionId();
            case THIRD_PARTY_HOSTED -> cap.getThirdPartyHosted().getVineId();
            case _NOT_IN_SCHEMA -> "?";
        };

        return "(" + capText + ")";
    }

    private String dumpCaps(StructList.Reader<RpcProtocol.CapDescriptor.Reader> capTable) {
        switch (capTable.size()) {
            case 0:
                return "";
            case 1:
                return "#0:" + dumpCap(capTable.get(0));
            default:
            {
                var text = "#0:" + dumpCap(capTable.get(0));
                for (int ii = 1; ii< capTable.size(); ++ii) {
                    text += ",#" + ii + ":" + dumpCap(capTable.get(ii));
                }
                return text;
            }
        }
    }

    String dump(String sender, RpcProtocol.Message.Reader message) {
        return switch (message.which()) {
            case CALL -> {
                var call = message.getCall();
                var iface = call.getInterfaceId();

                var interfaceName = String.format("0x%x", iface);
                var methodName = String.format("method#%d", call.getMethodId());
                var paramText = "<params>";
                var payload = call.getParams();
                var params = payload.getContent();
                var sendResultsTo = call.getSendResultsTo();

                var schema = this.schemas.get(iface);
                if (schema != null) {
                    interfaceName = schema.getDisplayName().toString();
                    if (schema.isInterface()) {

                        interfaceName = schema.getDisplayName().toString();
                        var interfaceSchema = schema.getInterface();

                        var methods = interfaceSchema.getMethods();
                        if (call.getMethodId() < methods.size()) {
                            var method = methods.get(call.getMethodId());
                            methodName = method.getName().toString();
                            var paramType = method.getParamStructType();
                            paramText = renderDynamic(paramType, params);
                            var resultType = method.getResultStructType();

                            if (call.getSendResultsTo().isCaller()) {
                                var questionId = call.getQuestionId();
                                setReturnType(call.getQuestionId(), resultType);
                            }
                        }
                    }
                }

                var target = switch (call.getTarget().which()) {
                    case IMPORTED_CAP -> "IMPORTED CAP [" + call.getTarget().getImportedCap() + "]";
                    case PROMISED_ANSWER -> "ANSWER [" + call.getTarget().getPromisedAnswer().getQuestionId() + "]";
                    case _NOT_IN_SCHEMA -> "<unknown target>";
                };

                yield sender + "(" + call.getQuestionId() + "): call " +
                        target + " <- " + interfaceName + "." +
                        methodName + "(" + paramText + ") [" +
                        dumpCaps(payload.getCapTable()) + "]" +
                        (sendResultsTo.isCaller() ? "" : (" sendResultsTo:" + sendResultsTo));
            }

            case RETURN -> {
                var ret = message.getReturn();
                var text = sender + "(" + ret.getAnswerId() + "): ";
                var returnType = getReturnType(ret.getAnswerId());

                yield switch (ret.which()) {
                    case RESULTS -> {
                        var payload = ret.getResults();
                        var returnText = renderDynamic(returnType, payload.getContent());
                        yield text + "return (" + returnText +
                                ") [" + dumpCaps(payload.getCapTable()) + "]"
                                + " releaseParamCaps?" + ret.getReleaseParamCaps()
                                + " takeFromOtherQuestion?" + ret.isTakeFromOtherQuestion()
                                + " cancelled?" + ret.isCanceled();
                    }
                    case EXCEPTION -> {
                        var exc = ret.getException();
                        yield text + "exception "
                                + exc.getType().toString() +
                                " " + exc.getReason();
                    }
                    default -> text + ret.which().name();
                };
            }

            case BOOTSTRAP -> {
                var restore = message.getBootstrap();
                setReturnType(restore.getQuestionId(), 0);
                yield sender + "(" + restore.getQuestionId() + "): bootstrap " +
                        restore.getDeprecatedObjectId();
            }

            case ABORT -> {
                var abort = message.getAbort();
                yield sender + ": abort "
                        + abort.getType().toString()
                        + " \"" + abort.getReason().toString() + "\"";
            }

            case RESOLVE -> {
                var resolve = message.getResolve();
                var id = resolve.getPromiseId();
                var text = switch (resolve.which()) {
                    case CAP -> {
                        var cap = resolve.getCap();
                        yield cap.which().toString();
                    }
                    case EXCEPTION -> {
                        var exc = resolve.getException();
                        yield exc.getType().toString() + ": " + exc.getReason().toString();
                    }
                    default -> resolve.which().toString();
                };
                yield sender + "(" + id + "): resolve " + text;
            }

            default -> sender + ": " + message.which().name();
        };
    }
}
