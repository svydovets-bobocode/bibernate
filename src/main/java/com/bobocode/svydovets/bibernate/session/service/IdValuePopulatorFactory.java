package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.constant.GenerationType.IDENTITY;
import static com.bobocode.svydovets.bibernate.constant.GenerationType.SEQUENCE;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.util.Map;

public class IdValuePopulatorFactory {
    private final Map<GenerationType, IdValuePopulator> idValuePopulatorMap;

    private static volatile IdValuePopulatorFactory instance;

    private IdValuePopulatorFactory() {
        this.idValuePopulatorMap =
                Map.of(IDENTITY, new IdentityIdValuePopulator(), SEQUENCE, new SequenceIdValuePopulator());
    }

    public static IdValuePopulatorFactory getInstance() {
        if (instance == null) {
            synchronized (IdValuePopulatorFactory.class) {
                if (instance == null) {
                    instance = new IdValuePopulatorFactory();
                }
            }
        }
        return instance;
    }

    public IdValuePopulator getIdValuePopulator(GenerationType idPopulatorType) {
        if (idValuePopulatorMap.containsKey(idPopulatorType)) {
            return idValuePopulatorMap.get(idPopulatorType);
        }
        throw new UnsupportedOperationException(
                "Unknown generation type, no populator found for " + idPopulatorType);
    }
}
