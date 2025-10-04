package com.vandenbreemen

import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.EmptyRequestResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.LoggingLevel
import io.modelcontextprotocol.kotlin.sdk.LoggingMessageNotification
import io.modelcontextprotocol.kotlin.sdk.Method.Defined
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.ToolAnnotations
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

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
                ),
                tools = ServerCapabilities.Tools(
                    listChanged = true,

                )
            )
        ),
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

    //  Example tool definition can be found here:
    //  https://github.com/modelcontextprotocol/kotlin-sdk/blob/main/samples/weather-stdio-server/src/main/kotlin/io/modelcontextprotocol/sample/server/McpWeatherServer.kt
    server.addTool(
        Tool(
            name = "example-tool",
            description = "An example tool that echoes input",
            title = "Example Tool",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("inputText") {
                        put("type", "string")
                        put("description", "Text to echo")
                    }
                },
                required = listOf("inputText")
            ),
            outputSchema = Tool.Output(
                properties = buildJsonObject {
                    putJsonObject("outputText") {
                        put("type", "string")
                        put("description", "Echoed text")
                    }
                },
                required = listOf("outputText")
            ),
            annotations = ToolAnnotations(
                "Example Tool"
            )
        )

    ) { toolRequest ->
        val inputText = toolRequest.arguments["inputText"]?.jsonPrimitive?.content ?: return@addTool CallToolResult(
            content = listOf(TextContent("inputText is required"))
        )

        CallToolResult(
            content = listOf(
                TextContent(
                    text = buildJsonObject {
                        put("outputText", "Echo: $inputText")
                    }.jsonObject.toString()
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

        //  Workaround for https://github.com/modelcontextprotocol/kotlin-sdk/issues/293#event-19961251030

        //  Take a look in types.kt for defined method types
        server.setRequestHandler<LoggingMessageNotification.SetLevelRequest>(
            method = Defined.LoggingSetLevel,

        ) { setLevelRq, _log ->
            EmptyRequestResult()
        }


        val closeSignal = CompletableDeferred<Unit>()
        server.onClose {
            closeSignal.complete(Unit)
        }
        closeSignal.await()
    }
}