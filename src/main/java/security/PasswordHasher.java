package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Deriva y verifica contrasenas con PBKDF2-HMAC-SHA256.
 * Cada contrasena usa una sal aleatoria propia y nunca se guarda en texto plano.
 */
public final class PasswordHasher {
  private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
  private static final String PREFIJO = "pbkdf2_sha256";
  private static final int ITERACIONES = 210_000;
  private static final int LONGITUD_CLAVE_BITS = 256;
  private static final int LONGITUD_SAL_BYTES = 16;
  private static final SecureRandom RANDOM = new SecureRandom();

  private PasswordHasher() {
  }

  /**
   * Genera un hash nuevo con una sal aleatoria.
   * Formato: pbkdf2_sha256$iteraciones$salBase64$hashBase64
   */
  public static String hash(String contrasena) {
    byte[] sal = new byte[LONGITUD_SAL_BYTES];
    RANDOM.nextBytes(sal);
    byte[] derivada = derivar(contrasena, sal, ITERACIONES);
    return PREFIJO + "$" + ITERACIONES + "$" + Base64.getEncoder().encodeToString(sal) + "$"
        + Base64.getEncoder().encodeToString(derivada);
  }

  /**
   * Verifica una contrasena contra un hash almacenado.
   * Devuelve false ante cualquier formato invalido o valor nulo.
   */
  public static boolean verificar(String contrasena, String almacenado) {
    if (contrasena == null || almacenado == null) {
      return false;
    }
    String[] partes = almacenado.split("\\$");
    if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
      return false;
    }
    try {
      int iteraciones = Integer.parseInt(partes[1]);
      byte[] sal = Base64.getDecoder().decode(partes[2]);
      byte[] esperada = Base64.getDecoder().decode(partes[3]);
      byte[] calculada = derivar(contrasena, sal, iteraciones);
      return MessageDigest.isEqual(esperada, calculada);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private static byte[] derivar(String contrasena, byte[] sal, int iteraciones) {
    PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), sal, iteraciones, LONGITUD_CLAVE_BITS);
    try {
      return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException("No se pudo derivar la contrasena", e);
    } finally {
      spec.clearPassword();
    }
  }
}
