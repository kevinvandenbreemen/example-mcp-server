# Training Project for learning to use MCP (Model Context Protocol)

This project is designed to help you learn how to use the Model Context Protocol (MCP) effectively. MCP is a protocol that allows for seamless interaction between different models and systems, enabling more efficient data exchange and processing.


# How to test this
You can use the inspector, via npx, like this:
```bash
npx @modelcontext/inspector ./gradlew run --quiet
```

# How do I Hook This Up to my Copilot MCP Servers?

You'll need to use the sh command with the correct working directory like this:

```json
      "example-server": {
        "command": "sh",
        "args": [
          "-c",
          "cd /path/where/you/checked/out/mcp-training && ./gradlew run --quiet"
        ],
        "env": {
          [Env vars from your Inspector tool]
        }
      }
```