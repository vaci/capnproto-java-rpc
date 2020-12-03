package org.capnproto.examples;

import org.capnproto.*;
import org.junit.Assert;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

public class CalculatorClient {

    public static void usage() {
        System.out.println("usage: host:port");
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            usage();
            return;
        }

        var endpoint = args[1].split(":");
        var address = new InetSocketAddress(endpoint[0], Integer.parseInt(endpoint[1]));
        try {
            var clientSocket = AsynchronousSocketChannel.open();
            clientSocket.connect(address).get();
            var rpcClient = new TwoPartyClient(clientSocket);
            var client = new org.capnproto.examples.Calc.Calculator.Client(rpcClient.bootstrap());

            {
                var request = client.evaluateRequest();
                request.getParams().getExpression().setLiteral(123);
                var evalPromise = request.send();
                var readPromise = evalPromise.getValue().readRequest().send();

                var response = rpcClient.runUntil(readPromise);
                Assert.assertTrue(response.get().getValue() == 123);
            }
        }
        catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
