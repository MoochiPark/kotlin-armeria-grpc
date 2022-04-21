package io.wisoft.account

import com.google.protobuf.util.JsonFormat
import com.linecorp.armeria.common.HttpData
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.MediaType
import com.linecorp.armeria.common.ResponseHeaders
import com.linecorp.armeria.server.annotation.Get

class AccountHttpService {
    @Get("/v1/accounts")
    fun getAccountAllV1(): HttpResponse {
        val accounts = AccountService.getAccountAll()
        return HttpResponse.of(MediaType.JSON, accounts.toJson())
    }

    @Get("/v2/accounts")
    fun getAccountAllV2(): HttpResponse {
        val accounts = AccountService.getAccountAll().toProto()
        return HttpResponse.of(ResponseHeaders.of(HttpStatus.OK), HttpData.wrap(accounts.toByteArray()))
    }
}
