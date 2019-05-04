package ru.endlesscode.rpginventory.configuration

class TestConfiguration : Configurable {

    var aString = "Lorem ipsum dolor sit amet."
    var anInt = 5

    override fun getHeader(): String? = null

    override fun getNodeName(): String = "TestNode"
}
