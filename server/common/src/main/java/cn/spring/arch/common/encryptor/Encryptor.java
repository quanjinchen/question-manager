package cn.spring.arch.common.encryptor;

public interface Encryptor {

    String encrypt(String plainData);

    String decrypt(String encryptedData);
}

