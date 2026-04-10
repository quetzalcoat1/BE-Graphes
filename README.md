# Graph & Algorithm project &mdash; INSA Toulouse

This is an INSA local copy of [Mikael CAPELLE's repository](https://gitea.typename.fr/INSA/be-graphes).

## How to start?

1. On your favorite platform, create a new empty repository of yours.
  - [INSA GitEtud](https://git.etud.insa-toulouse.fr/)
  - [Gitlab](https://gitlab.com/)
  - [Bitbucket](https://bitbucket.org/)
  - [Github](https://github.com/)

2. On your computer or on an INSA computer, clone your own repository (in the following, we call it MINE)
    ```bash
    git clone (here-the-https-URL-to-your-own-repository)
    ```

3. Somewhere else on the same computer, clone this repository (we call it THEIRS)
    ```bash
    git clone https://git.etud.insa-toulouse.fr/lebotlan/BE-Graphes.git
    ```

4. In this cloned directory (THEIRS), remove the `.git` folder.

5. Your repository (MINE) is initially empty. Move all the content of the BE-Graphes project (THEIRS) into it. Keep also the .gitignore file.

6. Commit your changes (MINE):
    ```bash
    git status
    git add .
    git status    ## Check the added files
    git commit -m "My initial commit"
    git push
    ```

