requisites=$(nix-store --query --requisites --quiet --quiet $(readlink -f $(type -p java)))
gsettings_schemas_path=$(echo $requisites | grep -o -E [^[:space:]]*gsettings-desktop-schemas[^[:space:]]*)
gtk_path=$(echo $requisites | grep -o -E [^[:space:]]*gtk\\+3[^[:space:]]*)
eval $(nix-store --print-env $(nix show-derivation $gsettings_schemas_path | tail -n+2 | head -n1 | cut -d\" -f2) | grep "export name")
gsettings_schemas_name=$name
eval $(nix-store --print-env $(nix show-derivation $gtk_path | tail -n+2 | head -n1 | cut -d\" -f2) | grep "export name")
gtk_name=$name
export XDG_DATA_DIRS=$gsettings_schemas_path/share/gsettings-schemas/$gsettings_schemas_name:$gtk_path/share/gsettings-schemas/$gtk_name:$XDG_DATA_DIRS
echo "$XDG_DATA_DIRS"
unset name
unset requisites
unset gsettings_schemas_path
unset gtk_path
unset gsettings_schemas_name
unset gtk_name

mvn javafx:run
