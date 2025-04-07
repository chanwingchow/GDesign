package com.chanwingchow.spider

import com.chanwingchow.database.Product
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

/**
 * 爬取产品。
 */
suspend fun getProducts(): List<Product> {
    val client = HttpClient {
        defaultRequest { url("https://category.dangdang.com") }
    }
    val text = client.get("https://category.dangdang.com/cid4002145.html").bodyAsText()
    val doc = Ksoup.parse(text)

    // 商品列表
    val lis = doc.getElementById("component_47")!!.children()

    val products = mutableListOf<Product>()
    for (li in lis) {
        val children = li.children()
        val firstChild = children[0]
        val img = firstChild.child(0)

        val id = li.attr("id").toLong()
        val name = firstChild.attr("title")
        val image = "https:${if (img.hasAttr("data-original")) img.attr("data-original") else img.attr("src")}"
        val points = (children[1].child(0).text().replace("¥", "").toDouble() * 1000).toInt()
        products.add(Product(id, name, image, points))
    }

    return products
}