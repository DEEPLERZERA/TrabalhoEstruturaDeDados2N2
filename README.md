
# Mental Health – ABB × AVL (VSCode project)

> Usa **ABB.java**, **AVL.java**, **NoAVL.java**, **Node.java** e **Tree.java** exatamente como você enviou (sem mudar pacote).
> O projeto está em **default package** para evitar conflitos de import.

## Estrutura
```
MH_ABB_AVL_Project/
├─ src/
│  ├─ ABB.java
│  ├─ AVL.java
│  ├─ NoAVL.java
│  ├─ Node.java
│  ├─ Tree.java
│  ├─ MHRecord.java
│  ├─ MHLoader.java
│  ├─ ABBSearch.java
│  ├─ Experiments.java
│  └─ App.java
└─ data/
   └─ mental_health_clean.csv
```

## Como rodar (VSCode ou terminal)
### Terminal
```bash
cd MH_ABB_AVL_Project
javac src/*.java
java -cp src App
```

- O programa vai carregar `data/mental_health_clean.csv`, montar ABB e AVL,
- executar **buscas** em ~5k chaves,
- e salvar `metrics.csv` na raiz (comparações via `Comparable` e tempos em nanos).

> Dica: ajuste o tamanho de `queries` em `App.java` se quiser um teste maior/menor.

## Observações
- **Comparações**: são contadas dentro de `MHRecord.compareTo` (vale para ABB e AVL sem alterar o seu código).
- **Rotações (AVL)**: sua implementação não expõe contador; por isso reportamos **apenas comparações e tempos**.
- **Search ABB**: como `ABB.java` não expõe busca pública, usamos `ABBSearch.search` iterativo com `Node`.
- **Tree.java** está incluído para referência/uso futuro (impressão/níveis etc.).
```


## Menu interativo
Rode `java -cp src App` e use as opções:
1. **Carregar/Recarregar CSV** (reseta estado e contadores, cria `metrics.csv` e `rotations.csv` com fase `load_inserts`)
2. **Benchmark de Busca** (escolha a quantidade de consultas; appende linha `search` em `metrics.csv`)
3. **Experimento de Remoção** (escolha o %; appende `remove` em `metrics.csv` e `removals` em `rotations.csv`)
4. **Gerar Análises** (5+ CSVs em `out_*.csv`)
5. **Status Atual** (registros, removidos, rotações acumuladas)
6. **Sair**
