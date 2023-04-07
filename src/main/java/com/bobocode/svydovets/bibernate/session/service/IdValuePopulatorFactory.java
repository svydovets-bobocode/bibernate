package com.bobocode.svydovets.bibernate.session.service;

import static com.bobocode.svydovets.bibernate.constant.GenerationType.IDENTITY;
import static com.bobocode.svydovets.bibernate.constant.GenerationType.SEQUENCE;

import com.bobocode.svydovets.bibernate.constant.GenerationType;
import java.util.Map;

/**
 * The IdValuePopulatorFactory class is responsible for creating and returning an instance of
 * IdValuePopulator based on the given GenerationType.
 */
public class IdValuePopulatorFactory {
    private final Map<GenerationType, IdValuePopulator> idValuePopulatorMap;

    public IdValuePopulatorFactory() {
        this.idValuePopulatorMap =
                Map.of(IDENTITY, new IdentityIdValuePopulator(), SEQUENCE, new SequenceIdValuePopulator());
    }

    /**
     * Returns the appropriate IdValuePopulator based on the given GenerationType.
     *
     * @param idPopulatorType the GenerationType to get the IdValuePopulator for.
     * @return the corresponding IdValuePopulator.
     * @throws UnsupportedOperationException if no IdValuePopulator is found for the given
     *     GenerationType.
     */
    public IdValuePopulator getIdValuePopulator(GenerationType idPopulatorType) {
        if (idValuePopulatorMap.containsKey(idPopulatorType)) {
            return idValuePopulatorMap.get(idPopulatorType);
        }
        throw new UnsupportedOperationException(
                "Unknown generation type, no populator found for " + idPopulatorType);
    }
}
