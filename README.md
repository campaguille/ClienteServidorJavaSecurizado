# 🔏 Cliente-Servidor Multihilo con Firma Digital DSA — Java

Aplicación de **chat unidireccional** (clientes → servidor) con arquitectura **multihilo** y mensajes autenticados mediante **firma digital DSA**. Cada cliente firma sus mensajes con su clave privada y el servidor verifica la autenticidad usando la clave pública del cliente.

---

## 📋 Descripción

El servidor acepta múltiples conexiones simultáneas, gestionando cada cliente en un **hilo independiente**. Antes de enviar mensajes, el cliente genera un par de claves DSA y envía su clave pública al servidor. A partir de ese momento, cada mensaje viaja acompañado de su firma digital, lo que permite al servidor verificar que el mensaje no ha sido alterado y que proviene del cliente legítimo.

### Flujo de la comunicación

```
Cliente                                    Servidor
  |                                            |
  |  genera par de claves DSA (1024 bits)      |
  |--- envía clave pública ─────────────────>  |
  |                               almacena clave pública
  |                                            |
  |  firma mensaje con clave privada           |
  |--- envía [firma] + [mensaje en claro] ──>  |
  |                               verifica firma con clave pública
  |                               muestra mensaje si es válido
  |                                            |
  |  envía "."                                 |
  |--- cierra conexión ─────────────────────>  |
```

---

## ✨ Características

- 🔏 **Firma digital DSA** — Los mensajes se firman con `SHA1withDSA`. El servidor verifica la integridad y autenticidad de cada mensaje.
- 🧵 **Multihilo** — Cada cliente se atiende en un `Thread` independiente, sin bloquear nuevas conexiones.
- 💬 **Chat unidireccional** — Los clientes envían mensajes al servidor; este los verifica y muestra en consola.
- 👥 **Multicliente** — El servidor acepta N clientes en paralelo mediante un bucle `accept()` continuo.
- 📡 **Sockets TCP** — Comunicación fiable con `ServerSocket` / `Socket` y serialización de objetos (`ObjectInputStream` / `ObjectOutputStream`).
- 🚪 **Cierre controlado** — Enviar `.` desconecta al cliente limpiamente.

---

## 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| **Java SE** | Lenguaje principal |
| `java.security.Signature` | Firma y verificación DSA |
| `java.security.KeyPairGenerator` | Generación del par de claves DSA (1024 bits) |
| `java.security.SecureRandom` | Fuente de aleatoriedad segura (`SHA1PRNG`) |
| `java.net.ServerSocket` / `Socket` | Comunicación TCP |
| `java.io.ObjectOutputStream/InputStream` | Serialización de objetos por el socket |
| `java.lang.Thread` + `Runnable` | Gestión multihilo del servidor |

---

## 📁 Estructura del proyecto

```
ClienteServidorSecurizado/
├── src/
│   ├── Cliente.java      # Cliente: genera claves DSA, firma y envía mensajes
│   └── Servidor.java     # Servidor: acepta conexiones, verifica firmas, multihilo
└── README.md
```

---

## 🚀 Cómo ejecutar

### Requisitos

- Java JDK 11 o superior
- Terminal o IDE (IntelliJ IDEA, Eclipse, NetBeans, VS Code)

### 1. Clonar el repositorio

```bash
git clone https://github.com/campaguille/ClienteServidorJavaSecurizado.git
cd ClienteServidorJavaSecurizado
```

### 2. Compilar

```bash
javac -d out src/Servidor.java src/Cliente.java
```

### 3. Arrancar el servidor

```bash
java -cp out Servidor
```

```
Servidor levantado en el puerto 5050
```

### 4. Conectar uno o varios clientes

En terminales separados:

```bash
java -cp out Cliente
```

```
Se ha conectado con el servidor
Se han levantado los flujos de comunicacion con el servidor
Claves generadas con exito
Clave enviada con exito
Introduzca el mensaje:
```

Escribe mensajes libremente. Escribe **`.`** para desconectarte.

---

## 📸 Ejemplo de uso

**Consola del servidor:**
```
Servidor levantado en el puerto 5050
Se han levantado los flujos de comunicacion con el cliente 1
Obtenida la clave del cliente 1
Mensaje verificado: Hola servidor! ( 1 )
Mensaje verificado: Todo funciona correctamente ( 1 )
Se han levantado los flujos de comunicacion con el cliente 2
Obtenida la clave del cliente 2
Mensaje verificado: Soy el segundo cliente ( 2 )
```

**Consola del cliente:**
```
Se ha conectado con el servidor
Se han levantado los flujos de comunicacion con el servidor
Claves generadas con exito
Clave enviada con exito
Introduzca el mensaje:
Hola servidor!
Mensaje y firma enviados con exito
Introduzca el mensaje:
.
Mensaje y firma enviados con exito
Se ha cerrado la conexion con exito
```

---

## 🔑 Detalles técnicos de seguridad

| Parámetro | Valor |
|---|---|
| Algoritmo de claves | **DSA** |
| Tamaño de clave | **1024 bits** |
| Algoritmo de firma | **SHA1withDSA** |
| Generador aleatorio | **SHA1PRNG** (`SecureRandom`) |
| Serialización | `ObjectOutputStream` / `ObjectInputStream` |

> **Nota:** La firma digital garantiza **autenticidad** e **integridad**, pero los mensajes viajan en claro por el socket. Para añadir **confidencialidad** (cifrado), se podría combinar con AES o usar TLS (`SSLServerSocket`).

---

## ⚠️ Posibles mejoras

- [ ] Cifrar el contenido del mensaje (AES + intercambio de clave simétrica)
- [ ] Migrar a `SSLServerSocket` para TLS completo
- [ ] Aumentar el tamaño de clave DSA a 2048 bits
- [ ] Sustituir `SHA1withDSA` por `SHA256withDSA`
- [ ] Añadir manejo de desconexión inesperada del cliente

---

## 🎓 Contexto académico

Proyecto desarrollado como parte del ciclo formativo de **Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM)**, en el módulo de **Programación de Servicios y Procesos**.

---

## 📄 Licencia

Este proyecto es de uso educativo y está disponible bajo la licencia [MIT](LICENSE).

---

## 👤 Autor

**Guillermo Campaña Herrero**
[![GitHub](https://img.shields.io/badge/GitHub-campaguille-181717?logo=github)](https://github.com/campaguille)
