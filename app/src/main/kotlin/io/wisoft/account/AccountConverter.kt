package io.wisoft.account

import io.wisoft.account.v1.AccountRole
import io.wisoft.account.v1.AccountV1
import io.wisoft.account.v1.GetAccountAllResponseV1
import io.wisoft.account.v1.SignUpRequestV1
import io.wisoft.account.v1.SignUpResponseV1

fun SignUpRequestV1.toEntity() = Account(
    id = id,
    name = name,
    password = password
)

fun Account.toProto(): SignUpResponseV1 = SignUpResponseV1.newBuilder()
    .setId(id)
    .build()

fun List<Account>.toProto(): GetAccountAllResponseV1 = GetAccountAllResponseV1.newBuilder()
    .addAllAccounts(
        this.map {
            AccountV1.newBuilder()
                .setId(it.id)
                .setName(it.name)
                .setRole(AccountRole.forNumber(it.id.split("_").last().toInt() % 2))
                .build()
        }
    ).build()