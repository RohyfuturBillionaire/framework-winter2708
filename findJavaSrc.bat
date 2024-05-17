setlocal enabledelayedexpansion

:: Demande à l'utilisateur de saisir le chemin du dossier source
set SRC_FOLDER=src\

:: Nom du fichier où les chemins seront enregistrés
set OUTPUT_FILE=java_files_list.txt

:: Vérifie si le dossier existe
if not exist "%SRC_FOLDER%" (
    echo Le dossier specifie n'existe pas.
    goto End
)

:: Initialise une variable pour stocker les chemins
set "ALL_PATHS="

:: Cherche tous les fichiers .java dans le dossier donné et sous-dossiers
echo Listing des fichiers .java dans %SRC_FOLDER% et sous-dossiers:
for /R "%SRC_FOLDER%" %%f in (*.java) do (
    echo %%f
    set "ALL_PATHS=!ALL_PATHS! %%f"
)

:: Écrit tous les chemins sur une seule ligne dans le fichier
echo !ALL_PATHS! > %OUTPUT_FILE%

echo Les chemins des fichiers ont été sauvegardés dans %OUTPUT_FILE% sur une seule ligne.

:End
endlocal
