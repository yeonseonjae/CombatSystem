package me.shark0822.combatSystem.stats.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class StatProviderManager {

    private static final List<Function<UUID, StatProvider>> providerFactories = new ArrayList<>();

    public static void registerProviderFactory(Function<UUID, StatProvider> factory) {
        providerFactories.add(factory);
    }

    public static void unregisterProviderFactory(Function<UUID, StatProvider> factory) {
        providerFactories.remove(factory);
    }

    public static StatAggregator createAggregator(UUID uuid) {
        List<StatProvider> providers = new ArrayList<>();

        providers.add(new ItemStatProvider(uuid));

        for (Function<UUID, StatProvider> factory : providerFactories) {
            StatProvider provider = factory.apply(uuid);
            if (provider != null) providers.add(provider);
        }

        return new StatAggregator(providers.toArray(new StatProvider[0]));
    }
}
