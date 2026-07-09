# java-demo-appweb-code

Webapp Java de démo (JSP + Servlet, servie par Tomcat 9). Sert à valider la chaîne
CI/CD → GHCR → GitOps sur une app compilée. Le déploiement vit dans `java-demo-appweb-k8s` ;
ce repo contient le code, le Dockerfile et les workflows.

## Structure

Projet Maven multi-module :

- `server` — logique métier (module `jar`), ex. `GreetingService`.
- `webapp` — interface web JSP (module `war`), dépend de `server`. Expose aussi `/metrics`
  (client Prometheus embarqué : métriques JVM + `app_http_requests_total`).

Le `Dockerfile` empaquète seulement le runtime : il copie `webapp/target/webapp.war` dans
Tomcat. **Il ne build pas le WAR** — c'est Maven qui le produit avant le `docker build`.

## Développement

Prérequis : JDK 11, Maven 3.9+.

```bash
mvn -B -ntp verify          # compile les 2 modules + tests JUnit (le gate de PR)
mvn -B -ntp clean package   # produit webapp/target/webapp.war
mvn -B test -pl server -Dtest=GreetingServiceTest   # un seul module / test

# Image locale
mvn -B -ntp clean package
docker build -t java-demo-appweb .
docker run -p 8081:8080 java-demo-appweb        # http://localhost:8081  et  /metrics
```

## CI/CD

Workflows dans `.github/workflows/` :

- `lint.yaml` — sur PR : hadolint + `mvn verify`. Gate avant merge.
- `build.yaml` — sur push `main`/`dev` : build le WAR, build+push l'image sur GHCR
  (`ghcr.io/mateplo/java-demo-appweb`) en deux tags `<branche>` et `<branche>-<sha>`,
  purge les vieilles images, scanne l'image (Trivy).
- `security.yaml` — CodeQL (java), Trivy (fs), gitleaks, + re-scan hebdo planifié.

Ensuite Argo CD Image Updater détecte le nouveau tag et le propage dans
`java-demo-appweb-k8s`. `main` → prod, `dev` → dev.

### À savoir

- Secret Actions **par-repo** `GHCR_CLEANUP_TOKEN` (PAT classique, `delete:packages`) requis
  par le job de purge — les secrets ne sont pas partagés entre repos.
- Après le premier push, rendre le package GHCR **public** : aucun `imagePullSecret` n'est
  configuré côté cluster, un package privé donne un `ImagePullBackOff`.
