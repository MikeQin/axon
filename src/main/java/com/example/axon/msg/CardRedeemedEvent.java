package com.example.axon.msg;

import io.axoniq.plugin.data.protection.annotation.SensitiveData;
import io.axoniq.plugin.data.protection.annotation.SensitiveDataHolder;
import io.axoniq.plugin.data.protection.annotation.SubjectId;

@SensitiveDataHolder
public record CardRedeemedEvent(
        @SubjectId String id,
        @SensitiveData(replacementValue = "hidden amount") int amount		
) {

}
