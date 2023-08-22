import kweb.*
import kweb.state.*
import kweb.plugins.fomanticUI.*
import io.github.oshai.kotlinlogging.KotlinLogging

val logger = KotlinLogging.logger {}

val counter = KVar(1)

data class Message(val sender: Int, val message: String)

val messages = ObservableList(listOf(Message(0, "Welcome")))

val names = mutableMapOf(0 to KVar("SYSTEM"))


fun main() {
    Kweb(port = 16097, debug = true, plugins = listOf(FomanticUIPlugin())) {
        doc.body {
            div(fomantic.ui.text.container) {
                table(fomantic.ui.celled.table) {
                    renderEach(messages) { message ->
                        tr {
                            td {
                                h4(fomantic.ui.header).text(names[message.sender]!!.map { name ->
                                    "$name: ${message.message}"
                                })
                            }
                        }
                    }
                }
            }

            ul {
                renderEach(messages) { message ->
                    li().text(names[message.sender]!!.map { name ->
                        "$name: ${message.message}"
                    })
                }
            }
            val id = counter.value++
            val name = KVar("Anon ${id}")
            names[id] = name
            val lastMessage = KVar("")

            div {
                input(type = InputType.text, placeholder = "Name").value = name
                val messageInput = input(type = InputType.text, placeholder = "What's on your mind?")
                messageInput.value = lastMessage
                fun addMessage() {
                    logger.info { "Adding message... ${lastMessage.value} from $id" }
                    messages.add(Message(id, lastMessage.value))
                    lastMessage.value = ""
                    messageInput.focus()
                }
                button().text("Send").on.click {
                    addMessage()
                }
                messageInput.on.keypress {
                    if (it.key == "Enter") {
                        addMessage()
                    }
                }

                messageInput.focus()
            }
        }
    }

}