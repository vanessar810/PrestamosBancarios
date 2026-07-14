 Desarrolladores: SPRING (JAVA) y Front-end (React) Makers

Optimización en sistemas financieros:
Imagina que trabajas en una aplicación bancaria que debe procesar transacciones en tiempo real. ¿Qué estrategias aplicarías para mejorar el rendimiento y la escalabilidad? Considera aspectos como concurrencia, caché y patrones de diseño.

Respuesta: para mejorar el rendimiento y la escalabilidad de este tipo de aplicaciones se debe considerar:
 usar procesamiento asincrónico para transacciones que no requieren respuesta inmediata, emplear thread pools para administrar los recursos de forma eficiente, proteger los datos compartidos mediante mecanismos de sincronización para evitar condiciones de carrera. 
Implementar caché para almacenamiento de información que se consulta frecuentemente y políticas de expiración.
Utilizar diferentes patrones de diseño como Observer para notificar a otros servicios cuando se complete una transacción, Strategy para los diferentes métodos de pago, y el patrón Repository para desacoplar el acceso a los datos de la lógica del negocio. 
ser stateless, optimizar las consultas a la base de datos, usar colas de mensajes y utilizar recursos de infraestructura como un balanceador de carga.
se debe garantizar consistencia y seguridad en las transacciones. 


2. Seguridad en APIs financieras:
cómo protegerías una API que maneja información sensible de cuentas bancarias contra ataques como inyección SQL, CSRF, XSS y otros ataques comunes.

Respuesta:
- Se debe usar consultas parametrizadas, o usas un ORM como Hibernate o JPA, validar la entrada del usuario, evitar consultas SQL concatenando cadenas. 
- Sanitizar el HTML antes de mostrarlo, validar entradas, usar content security policy. 
- Uso de APIs Rest con JWT, atributos sameSite en cookies, verificar encabezados de la petición, limitar intentos de inicio de sesión, bloqueos temporales de cuenta, CAPTCHA, autenticación multifactor o servicios como Cloudflare, usar peticiones HTTPS/TLS.
		
3. Transacciones en sistemas distribuidos:
En un sistema bancario distribuido, ¿cómo implementarías la consistencia y el manejo de errores en una API que procesa transferencias entre cuentas en diferentes servicios?
Respuesta: 
Las transacciones sobre base de datos deben cumplir atomicidad, ser indivisibles, consistentes, aisladas y persistir en un estado exitoso y no permitir estados inválidos, si la transacción falló en algún punto intermedio se debe hacer rollback. 
El patrón Saga permite a varios microservicios manejar transacciones permitiendo acciones compensatorias si algún microservicio llega a fallar en medio del proceso, y que esto no registre estados inválidos.
La API también debería evitar errores de transacciones duplicadas, aplicando idempotencia, debe devolver códigos HTTP apropiados, registrar logs y tablas de auditoría, se debería implementar reintentos para errores temporales y circuit breaker para evitar que los fallos se propaguen si un servicio no está disponible.


4. Pruebas unitarias y de integración:
Describe cómo diseñarías una suite de pruebas que asegure la correcta operación de una API bancaria, considerando tanto pruebas unitarias como de integración. ¿Qué herramientas utilizarías?

Respuesta:
El primer paso para diseñar las pruebas sería hacer pruebas unitarias para validar la lógica de negocio de forma aislada, por ejemplo verificar la salida de los métodos de lectura de saldo, calcular intereses, hacer validaciones y manejo de excepciones, he utilizado la herramienta Junit 5, pero también sirve Mockito. Luego haría pruebas de integración para verificar el comportamiento en conjunto de los controladores, servicios y repositorios, verificando persistencia en base de datos, autenticación y respuestas HTTP, para esto se puede usar Spring boot test o Mockvc. En ambas pruebas se debe verificar los casos de éxito y de error. 

5. Front-end:
En una aplicación bancaria que muestra el saldo de las cuentas, ¿cómo gestionarías el estado y la autenticación en el front-end, garantizando la seguridad y la coherencia de los datos?
Respuesta:
Usuaria useState en React para manejar estados locales como formularios y mensajes de error, useEffect para obtener información desde la API cuando el componente carga o cuando cambie el usuario autenticado. Para el estado global de autenticación se debe usar Context API o Redux para que toda la aplicación conozca la sesión.
implementa JWT para autenticación, enviando el token el encabezado Authorization de cada petición y usando siempre el protocolo HTTPS para proteger la comunicación, no almacenaría información sensible en el navegador ni haría transacciones con ella en texto plano, los saldos siempre se deben consultar directamente sobre la base de datos y el backend válida los permisos de usuario antes de enviar respuestas. 
La interfaz de usuario siempre debe reflejar la información almacenada en la base de datos y evitar mostrar datos desactualizados o manipulados. 

6. SPRING BOOT
Que pasa en una aplicación internamente cuando usas la anotación @SpringBootApplication y cómo afecta el arranque de una aplicación?
Respuesta: 
@SpringBootApplication combina tres anotaciones de Spring, indicando que esa clase tiene la configuración principal de la aplicación, que realice la configuración automática de los componentes necesarios de las dependencias que necesite el proyecto, y escanear clases con anotaciones como Component, Service, Repository, Controller y RestController, para registrarlas como beans en Spring. 

El arranque de la aplicación se vuelve más simple gracias a esta anotación, ya que configura automáticamente parte de la infraestructura, reduce configuración manual e inicializa los componentes necesarios, crea el ApplicationContext y escanea paquetes para detectar servicios, controladores y repositorios, e inyecta sus dependencias. 

¿Cómo funciona el ciclo de vida de un beans en Spring y cómo podrías intervenir en él?
Respuesta: 
Un bean es un objeto creado por Spring, lo almacena y lo administra. Para crearlo se usan anotaciones como @Component, @Service, @Repository, @Controller, @RestController o a través de métodos anotados con @Bean, se hace su instanciación, se inyectan las dependencias necesarias, se inicializa y ahora se puede usar, cuando la aplicación termina su ejecución, se destruye el bean y liberar sus recursos. 

Se puede intervenir en el ciclo de vida de un bean usando anotaciones como @PostConstruct para ejecutar lógica después de la inyección de dependencias, @PreDestroy para realizar tareas de limpieza antes de que el Bean sea eliminado. 

¿Cómo personalizarías el comportamiento de la auto-configuración en Spring Boot sin romper la filosofía de 'convención sobre configuración'?
Respuesta: 

Dado que Spring Boot sigue el principio de ‘convención sobre configuración’ por lo que intentaría aprovechar al máximo la auto - configuración y personalizar únicamente los aspectos necesarios.  Para personalizar este comportamiento se puede usar application.properties o application.yml para modificar parámetros como la conexión a la base de datos, puerto del servidor o la configuración de seguridad. Para comportamientos específicos se define un Bean personalizado que permita utilizar la implementación cuando sea necesario. 
                                          
