package com.suresell.mscoreapp.application.usecase;

import com.suresell.mscoreapp.application.dto.AccountReceivable;
import com.suresell.mscoreapp.application.dto.DebtTransaction;
import com.suresell.mscoreapp.domain.model.AccountReceivableEntity;
import com.suresell.mscoreapp.domain.model.DebtTransactionEntity;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountReceivableEntityMapper {

    @Mapping(target = "transactions", ignore = true)
    AccountReceivable toDomain(AccountReceivableEntity entity);

    @Mapping(target = "transactions", ignore = true)
    AccountReceivableEntity toEntity(AccountReceivable domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "transactions", ignore = true)
    void updateEntity(@MappingTarget AccountReceivableEntity entity, AccountReceivable domain);

    DebtTransaction transactionToDomain(DebtTransactionEntity entity);

    @Mapping(target = "accountId", source = "accountId")
    DebtTransactionEntity transactionToEntity(DebtTransaction domain, String accountId);
}
