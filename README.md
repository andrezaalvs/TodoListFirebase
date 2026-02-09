# To Do List com Firebase

**Universidade Federal de Uberlândia**  
**Sistemas de Informação**  
**Programação para Dispositivos Móveis**

---

**Andreza Batista Alves** — 12311BSI246  
**Keila Almeida Santana** — 12321BSI213  

Uberlândia, 2025

---

## Resumo

Este documento apresenta a documentação técnica do aplicativo **ToDo List com Firebase**, desenvolvido para a plataforma Android utilizando **Kotlin**, **Jetpack Compose** e os serviços **Firebase Authentication** e **Firebase Firestore**. O aplicativo permite que usuários autenticados gerenciem suas tarefas pessoais, garantindo persistência em nuvem, recuperação do estado entre sessões e separação dos dados por usuário.

São descritos de forma objetiva o modelo de dados, a arquitetura adotada, as dificuldades encontradas e possíveis melhorias futuras.

---

## 1. Introdução

O gerenciamento de tarefas por meio de aplicações móveis é uma prática comum no cotidiano, auxiliando usuários na organização de atividades pessoais e profissionais. Com esse objetivo, foi desenvolvido um aplicativo ToDo List para Android, com suporte à autenticação de usuários e persistência em nuvem.

A aplicação permite que cada usuário, após login ou cadastro, visualize e gerencie exclusivamente suas próprias tarefas, que são armazenadas no Firebase Firestore e recuperadas automaticamente em acessos posteriores, atendendo aos requisitos propostos no trabalho.

---

## 2. Funcionalidades do Aplicativo

- Cadastro de usuários utilizando **Firebase Authentication** (e-mail e senha)
- Login de usuários previamente cadastrados
- Criação de novas tarefas
- Marcação de tarefas como concluídas
- Exclusão de tarefas
- Separação visual entre tarefas **"A Fazer"** e **"Feitas"**
- Persistência automática das tarefas em nuvem
- Recuperação das tarefas associadas ao usuário autenticado
- Logout do usuário

---

## 3. Modelo de Dados e Persistência

Os dados são armazenados no **Firebase Firestore**, organizados de forma associada ao usuário autenticado, utilizando o identificador único (`uid`) fornecido pelo Firebase Authentication.

A estrutura adotada segue o padrão:

```
users/{uid}/tasks/{taskId}
```

Cada tarefa contém informações básicas, como título da tarefa, status de conclusão e data de atualização, permitindo controle simples e eficiente da lista.

### 3.1 Persistência em Nuvem

A persistência é realizada por meio do Firebase Firestore, garantindo que todas as tarefas criadas, alteradas ou removidas sejam salvas na nuvem. Ao abrir o aplicativo, o sistema recupera automaticamente as tarefas associadas ao usuário logado, mantendo o estado da aplicação entre diferentes sessões e dispositivos.

---

## 4. Arquitetura do Sistema

O projeto segue o padrão arquitetural **MVVM** (Model–View–ViewModel), adotando boas práticas recomendadas para aplicações Android modernas.

| Camada | Responsabilidade |
|--------|------------------|
| **View** | Telas desenvolvidas com Jetpack Compose, responsáveis pela interface e interação com o usuário. |
| **ViewModel** | Gerenciamento do estado da aplicação e regras de negócio. |
| **Model/Repository** | Comunicação com Firebase Authentication e Firebase Firestore. |

Essa organização contribui para maior clareza do código, manutenção e separação de responsabilidades.

---

## 5. Uso de LLMs no Desenvolvimento

Ferramentas baseadas em **Modelos de Linguagem de Grande Escala (LLMs)** foram utilizadas como apoio durante o desenvolvimento do projeto. As LLMs auxiliaram principalmente:

- Na integração do Firebase Authentication, especialmente na implementação do login
- Na resolução de erros relacionados a bibliotecas e dependências
- Na definição de cores e ícones da interface
- Na implementação do modo escuro (tema noturno)
- Na ocultação da senha no campo de autenticação
- Na criação da separação de telas para tarefas "A Fazer" e "Feitas"

O uso das LLMs contribuiu para acelerar o aprendizado e resolver dificuldades técnicas, sempre com validação manual das soluções aplicadas.

---

## 6. Dificuldades Encontradas

Durante o desenvolvimento, foram enfrentadas dificuldades relacionadas ao primeiro contato com o Firebase, que inicialmente apresentou erros de configuração e integração. Também houve desafios devido ao pouco domínio prévio do desenvolvimento Android, da linguagem Kotlin e do uso do Jetpack Compose.

Além disso, a definição do design e da interface do aplicativo, como escolha de cores, fontes e organização visual, exigiu ajustes e experimentação. Essas dificuldades foram superadas gradualmente com estudos, testes práticos e apoio das LLMs utilizadas ao longo do projeto.

---

## 7. Melhorias Futuras

Como possibilidades de evolução da aplicação, destacam-se:

- Implementação de funcionamento offline com cache local
- Edição do conteúdo das tarefas
- Filtros e ordenação das tarefas
- Testes automatizados
- Notificações de lembrete

---

## 8. Considerações Finais

O aplicativo ToDo List com Firebase atende aos requisitos estabelecidos no trabalho, oferecendo autenticação de usuários, persistência em nuvem e recuperação automática das tarefas. A utilização do Firebase em conjunto com Jetpack Compose e a arquitetura MVVM proporcionou uma aplicação funcional, organizada e alinhada aos conceitos abordados na disciplina.

---

## Tecnologias

- **Kotlin**
- **Jetpack Compose**
- **Firebase Authentication**
- **Firebase Firestore**
- **Arquitetura MVVM**
