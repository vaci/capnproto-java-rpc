@0x84cb3c49d98eccc8;

using Java = import "/capnp/java.capnp";
$Java.package("org.capnproto.rpctest");
$Java.outerClassname("TestGenerics");

#struct Aaa(X) {
#}

interface Bbb(Y) {
   m0 @0 () -> (r1: Y);
#   m1 @1 (p1: Y);
#   m2 @2 (p1: Y) -> (r1: Y);
}

#interface Ccc {
#   m0 @0 [Y] () -> (r1: Y);
#   m1 @1 [Y] (p1: Y);
#   m2 @2 [Y] (p1: Y) -> (r1: Y);
#}
