package ru.endlesscode.rpginventory.configuration

class TestConfiguration : Configurable {

    override val header: String? = null
    override val nodeName: String = "TestNode"

    var aString = "Lorem ipsum dolor sit amet."
    var anInt = 5

}
