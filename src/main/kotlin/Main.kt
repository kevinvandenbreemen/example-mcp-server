package com.vandenbreemen

import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {


    val server = Server(
        serverInfo = Implementation(
            name = "example-server",
            version = "1.0.0"
        ),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                resources = ServerCapabilities.Resources(
                    subscribe = true,
                    listChanged = true
                )
            )
        )
    )

// Add a resource
    server.addResource(
        uri = "file:///example.txt",
        name = "Example Resource",
        description = "An example text file",
        mimeType = "text/plain"
    ) { request ->
        ReadResourceResult(
            contents = listOf(
                TextResourceContents(
                    text = "This is the content of the example resource.",
                    uri = request.uri,
                    mimeType = "text/plain"
                )
            )
        )
    }

// Start server with stdio transport
    //  See also https://github.com/modelcontextprotocol/kotlin-sdk/blob/main/samples/kotlin-mcp-server/src/jvmMain/kotlin/main.jvm.kt
    //  for the example code used to get this up and running
    val transport = StdioServerTransport(
        inputStream = System.`in`.asSource().buffered(),
        outputStream = System.out.asSink().buffered()
    )
    runBlocking {
        server.connect(transport)
        val done = Job()
        server.onClose {
            done.complete()
        }
        done.join()
        println("Server closed")
    }
}