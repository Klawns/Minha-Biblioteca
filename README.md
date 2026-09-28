# Minha Biblioteca

Aplicação de biblioteca pessoal criada para organizar e armazenar livros em PDF de forma simples.

O projeto possui uma interface desenvolvida em **Angular**, uma API em **Spring Boot** e utiliza **PostgreSQL** para armazenamento das informações. Os arquivos dos livros e suas capas são armazenados utilizando **Cloudflare R2**.

## Funcionalidades

- Adicionar livros em PDF
- Organizar uma biblioteca pessoal
- Visualizar informações dos livros
- Armazenar PDFs e capas
- Acompanhar livros disponíveis na biblioteca
- Acessar a aplicação pelo computador, celular ou tablet na mesma rede

## Tecnologias utilizadas

- Angular
- Java
- Spring Boot
- PostgreSQL
- Cloudflare R2
- Docker

## Executando o projeto

Para executar a aplicação, é necessário ter o **Docker Desktop** instalado e em execução.

Antes de iniciar o projeto, configure as credenciais do Cloudflare R2 no arquivo:

`biblioteca-backend/.env`

As seguintes informações precisam ser configuradas:

```env
R2_ENDPOINT=
R2_ACCESS_KEY_ID=
R2_SECRET_ACCESS_KEY=
R2_BUCKET_NAME=
```

Depois da configuração, execute o seguinte comando na raiz do projeto:

```bash
docker compose up --build -d
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

## Acessando pelo celular ou tablet

Também é possível acessar a aplicação por outro dispositivo conectado à mesma rede Wi-Fi.

Descubra o endereço IP do computador onde a aplicação está sendo executada e acesse:

```text
http://IP_DO_COMPUTADOR:8080
```

Exemplo:

```text
http://192.168.1.100:8080
```

No Windows, o endereço IP pode ser encontrado executando:

```powershell
ipconfig
```

Procure pelo endereço **IPv4** da rede utilizada.

## Comandos úteis

Para visualizar os logs da aplicação:

```bash
docker compose logs -f
```

Para encerrar os containers:

```bash
docker compose down
```

Os dados da aplicação são mantidos mesmo após os containers serem encerrados.

> Evite utilizar `docker compose down -v` caso queira preservar os livros e os dados armazenados.

## Banco de dados

Por padrão, o projeto utiliza:

```text
Usuário: myuser
Senha: secret
Banco: mydatabase
```

Essas configurações podem ser alteradas por meio de variáveis de ambiente.

## Desenvolvimento

O projeto é dividido em frontend e backend:

```text
biblioteca-frontend
biblioteca-backend
```

Cada diretório possui suas próprias configurações para execução durante o desenvolvimento.

## Sobre o projeto

O **Minha Biblioteca** foi desenvolvido como um projeto pessoal para praticar o desenvolvimento de uma aplicação full stack, integração entre frontend e backend, armazenamento de arquivos em nuvem, persistência de dados e uso de containers.