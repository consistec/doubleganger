In this tutorial, we will implement a small console client application as a typical use case of our synchronization framework. We will synchronize two databases, the server and the client, using the HTTP protocol to transfer data. Therefore, we will also develop a minimalistic web application to handle client HTTP requests and delegate them to the doubleganger framework on the server's side.

Doubleganger does not rely on any particular transfer protocol, so you are free to use your own in real world applications. We use HTTP in this small proof-of-concept, but it would also be sufficient for the client to have direct access to the server.

You will need the following setup for this tutorial:

 * JDK 1.6
 * Maven 2+
 * PostgreSQL 8.1+
 * IDE (we use IntelliJ)
 * the synchronization framework

**client console application**

 * a client database ("client")

**server web application**

 * Tomcat 7
 * a server database ("server")

Note:

*For simplicity's sake, we use different databases on the same PostgreSQL instance. You could also run two separate PostgreSQL instances (ports 5432 and 5433 on the your machine or use two different machines) to stay closer to real world configurations.*


