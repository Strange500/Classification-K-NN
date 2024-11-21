{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {

  nativeBuildInputs = [
    pkgs.maven
    (pkgs.jdk17.override { enableJavaFX = true; })
    pkgs.kdePackages.qtwayland
    pkgs.kdePackages.qtsvg
    ];
    
    EDITOR = "nvim";
    VISUAL = "nvim";
}
