{
description = "Basic Flake";

outputs = { self, nixpkgs }:
 let
  system = "x86_64-linux";
  pkgs = import nixpkgs {
   inherit system;
   url = "nixpkgs/nixos-24.05";
  };

  deps = with pkgs; [
   #put dependencies here :)
   jdk21_headless
  ];

  non-deps = with pkgs; [
   #anything not dependency, but usefull (like editors)
   jetbrains.idea-community-bin
  ];

  #put build instructions here :)

 in
 {
  #give me a shell
  devShells.${system}.default = pkgs.mkShell {
   packages = deps ++ non-deps;
  };

 };
}
