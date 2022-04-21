package io.wisoft.account

import com.google.protobuf.Empty
import io.grpc.Status
import io.grpc.StatusException
import io.wisoft.account.v1.AccountRouteGrpcKt
import io.wisoft.account.v1.GetAccountAllResponseV1
import io.wisoft.account.v1.GetProfileStreamResponseV1
import io.wisoft.account.v1.SignUpRequestV1
import io.wisoft.account.v1.SignUpResponseV1
import kotlinx.coroutines.flow.Flow
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class AccountGrpcService : AccountRouteGrpcKt.AccountRouteCoroutineImplBase() {
    override suspend fun signUpV1(request: SignUpRequestV1): SignUpResponseV1 = try {
        AccountService.signUp(request.toEntity()).toProto()
    } catch (e: Exception) {
        logger.error { e.stackTraceToString() }
        throw StatusException(Status.UNKNOWN.withDescription(e.stackTraceToString()))
    }

    override fun getProfileStreamV1(request: Empty): Flow<GetProfileStreamResponseV1> = try {
        AccountService.getProfileStream()
    } catch (e: Exception) {
        logger.error { e.stackTraceToString() }
        throw StatusException(Status.UNKNOWN.withDescription(e.stackTraceToString()))
    }

    override suspend fun getAccountAllV1(request: Empty): GetAccountAllResponseV1 = try {
        AccountService.getAccountAll().toProto()
    } catch (e: Exception) {
        logger.error { e.stackTraceToString() }
        throw StatusException(Status.UNKNOWN.withDescription(e.stackTraceToString()))
    }
}