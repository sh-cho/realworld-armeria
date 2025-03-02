package io.realworld.di;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import dagger.Module;
import dagger.Provides;
import io.realworld.security.JwtService;
import jakarta.inject.Singleton;

@Module
interface AuthModule {

    // just for local dev
    @Singleton
    @Provides
    static KeyPair keyPair() {
        final KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        kpg.initialize(2048);

        return kpg.generateKeyPair();
    }

    @Singleton
    @Provides
    static RSAPublicKey rsaPublicKey(KeyPair keyPair) {
        return (RSAPublicKey) keyPair.getPublic();
    }

    @Singleton
    @Provides
    static RSAPrivateKey rsaPrivateKey(KeyPair keyPair) {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    @Singleton
    @Provides
    static JwtService jwtService(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        return new JwtService(rsaPublicKey, rsaPrivateKey);
    }
}
