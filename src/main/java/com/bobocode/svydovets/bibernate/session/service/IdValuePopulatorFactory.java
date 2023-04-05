package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.constant.GenerationType.IDENTITY;
import static com.bobocode.svydovets.bibernate.constant.GenerationType.SEQUENCE;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.util.Map;

public class IdValuePopulatorFactory {
    private final Map<GenerationType, IdValuePopulator> idValuePopulatorMap;

    public IdValuePopulatorFactory() {
        this.idValuePopulatorMap =
                Map.of(IDENTITY, new IdentityIdValuePopulator(), SEQUENCE, new SequenceIdValuePopulator());
    }

    public IdValuePopulator getIdValuePopulator(GenerationType idPopulatorType) {
        if (idValuePopulatorMap.containsKey(idPopulatorType)) {
            return idValuePopulatorMap.get(idPopulatorType);
        }
        throw new UnsupportedOperationException(
                "Unknown generation type, no populator found for " + idPopulatorType);
    }
}
