package ru.endlesscode.rpginventory.misc

import ru.endlesscode.rpginventory.configuration.Configurable

class TestConfiguration : Configurable {

    var aString = "Lorem ipsum dolor sit amet."
    var anInt = 5

    override fun getHeader(): String? = null

    override fun getNodeName(): String = "TestNode"
}
