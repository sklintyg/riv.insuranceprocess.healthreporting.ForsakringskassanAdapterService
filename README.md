# Inera INTYG riv.insuranceprocess.healthreporting.ForsakringskassanAdapterService

Detta är en fork av https://github.com/skltp-anpassningstjanster/riv.insuranceprocess.healthreporting.ForsakringskassanAdapterService

## Bygge och deploy
Då denna artefakt rör på sig ytterst sällan så har vi inget Jenkins-jobb för den.

Istället får man konfigurera upp sin lokala miljö och deploya via maven. Gör så hör:

#### 1. Bygg lokalt
För säkerhets skull - bygg och installera lokalt:

    mvn clean package install
    
Med fördel uppdatera till aktuell version i _/common/build.gradle_ t.ex. _2.2-RC3-INTYG_ och bygg sedan hela common.

Fortsätt när allt ser bra ut.

#### 2. Förbered .m2/settings.xml

Gå till motsv ~/.m2/settings.xml och leta reda på följande:

    <server>
          <id>inera-release-repository</id>
          <username>USERNAME</username>
          <password>PASSWORD</password>
    </server>

Tillse att användarnamn och lösenord stämmer överens med vår Nexus3. Spara. Kom ihåg att ändra tillbaka när du är klar!

#### 3. Kopiera filen till lokal sökväg
För säkerhets skull, kopiera filen så du har den i katalogen.

    cp FkEintygAdapterIC-validator/target/FkEintygAdapterIC-validator-2.2-RC3-INTYG.jar .

#### 4. Kör mvn deploy:deploy-file

    mvn deploy:deploy-file -DgroupId=se.skltp.adapterservices.insuranceprocess.healthreporting \
        -DartifactId=ForsakringskassanEintygAdapter \
        -Dversion=2.2-RC3-INTYG \
        -Dpackaging=jar \
        -Dfile=FkEintygAdapterIC-validator-2.2-RC3-INTYG.jar \
        -DgeneratePom=true \
        -DrepositoryId=inera-release-repository \
        -Durl=https://build-inera.nordicmedtest.se/nexus/repository/releases/
        
        
KLART!

## Uppdatera från original-repot
För att uppdatera detta repo, läs på lite om forks.


