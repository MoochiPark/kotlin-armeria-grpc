package io.wisoft

import com.google.protobuf.Empty
import com.google.protobuf.Message
import com.google.protobuf.util.JsonFormat
import com.linecorp.armeria.client.WebClient
import com.linecorp.armeria.common.HttpHeaderNames
import com.linecorp.armeria.common.HttpMethod
import com.linecorp.armeria.common.RequestHeaders
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.mpp.timeInMillis
import io.wisoft.account.v1.AccountRouteGrpcKt
import io.wisoft.account.v1.GetAccountAllResponseV1
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class LauncherKtTest : FunSpec({
    var start = 0L

    beforeSpec {
        newServerAndStart()
    }

    beforeEach {
        start = timeInMillis()
    }

    afterEach {
        val end = timeInMillis()
        logger.info { "메시지 받는데 걸린 시간 : ${end - start}ms" }
    }

    test("gRPC로 데이터를 받을 수 있다.") {
        val channel = NettyChannelBuilder.forAddress("localhost", 8080)
            .usePlaintext()
            .build()
        val stub = AccountRouteGrpcKt.AccountRouteCoroutineStub(channel)
        val response = stub.getAccountAllV1(Empty.getDefaultInstance())

        response.accountsList.size shouldBe 100
    }

    test("json 메시지로 데이터를 받을 수 있다.") {
        val url = "http://localhost:8080"
        val client = WebClient.builder(url)
            .build()
        val header = RequestHeaders.builder()
            .method(HttpMethod.POST)
            .add(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8; protocol=gRPC")
            .path("/io.wisoft.account.v1.AccountRoute/getAccountAllV1")
            .build()
        val actual = client
            .execute(header, Empty.getDefaultInstance().toJson())
            .aggregate()
            .join()

        logger.info { actual.contentUtf8() }

        actual.content().length() shouldBe 4198
    }

    test("http로 데이터를 받을 수 있다.") {
        val url = "http://localhost:8080"
        val client = WebClient.builder(url)
            .build()
        val header = RequestHeaders.builder()
            .method(HttpMethod.GET)
            .add(HttpHeaderNames.CONTENT_TYPE, "application/json;")
            .path("/v1/accounts")
            .build()
        val actual = client
            .execute(header)
            .aggregate()
            .join()
        logger.info { actual.contentUtf8() }

        actual.content().length() shouldBe 6203
    }

    test("http + protobuf byte로 데이터를 받을 수 있다.") {
        val url = "http://localhost:8080"
        val client = WebClient.builder(url)
            .build()
        val header = RequestHeaders.builder()
            .method(HttpMethod.GET)
            .path("/v2/accounts")
            .build()
        val actual = client
            .execute(header)
            .aggregate()
            .join()
        val response = GetAccountAllResponseV1.parseFrom(actual.content().array())
        logger.info { response.toJson() }

        actual.content().length() shouldBe 2084
    }
})

inline fun <reified T : Message> T.toJson(): String = JsonFormat.printer().print(this)