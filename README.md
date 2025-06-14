# Documentação do Projeto RemoteList-Client (Cliente)

Este repositório contém o cliente Swing para o RemoteList, permitindo manipular listas via GUI.

## 1. Pré-requisitos

* Java JDK 21
* Maven 3.6+

## 2. Estrutura do Repositório

```
remotelist-client/
├─ pom.xml
└─ src/
   ├─ main/java/
   │  └─ br/edu/ifpb/remotelist/
   │     ├─ RemoteList.java           # interface RMI (mesma do servidor)
   │     └─ RemoteListClientFrame.java  # GUI Swing
   └─ resources/                      # (opcional)
```

## 3. Build

```bash
cd remotelist-client/
mvn clean package
```

Gera o JAR executável sombreado em:

```
target/remotelist-client-<versão>-shaded.jar
```

## 4. Execução

```bash
java -jar target/remotelist-client-<versão>-shaded.jar <host> <porta>
```

* **host**: endereço do servidor (padrão `localhost`)
* **porta**: porta RMI (padrão `1099`)

## 5. Operação

A interface oferece:

* Campo **List ID**: identifica a lista (string).
* Campo **Value**: valor para `append`.
* Campo **Index**: índice para `get`.
* Botões:

  * **Append**: insere valor na lista.
  * **Get**: mostra valor no índice.
  * **Remove**: remove último elemento.
  * **Size**: exibe tamanho da lista.
  * **Refresh**: recarrega toda a lista.

## 6. Troubleshooting

* **ClassNotFoundException**: verifique se `RemoteList.java` está no mesmo pacote de ambos.
* **ConnectException**: assegure que o servidor esteja ativo em `host:porta`.

---
