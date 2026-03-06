"use client"; // Avisa ao Next.js que esta página tem interatividade

import { useState } from "react";
import { Input } from "@/components/Input";
import { Button } from "@/components/Button";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");

  // Função que roda quando clicamos em "Entrar" usando o FETCH NATIVO
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault(); 
    setErro(""); 

    try {
      // Usando o fetch nativo do JavaScript
      const resposta = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email,
          senha: senha,
        }),
      });

      // O fetch não cai no "catch" automaticamente se a senha estiver errada (erro 403), 
      // então precisamos checar manualmente se a resposta foi "ok" (código 200)
      if (!resposta.ok) {
        throw new Error("Falha na autenticação");
      }

      // Convertendo a resposta para JSON
      const dados = await resposta.json();
      
      const token = dados.token;
      console.log("🔑 Sucesso! O seu Token é:", token);
      alert("Login realizado com sucesso! Olhe o Console (F12).");

    } catch (error) {
      console.error("Erro no login:", error);
      setErro("E-mail ou senha incorretos. Tente novamente.");
    }
  };

  return (
    <main className="min-h-screen flex items-center justify-center bg-zinc-950 p-4">
      <div className="w-full max-w-md bg-zinc-900 rounded-2xl shadow-2xl border border-zinc-800 p-8">
        
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-emerald-500">
            Personal System
          </h1>
          <p className="text-zinc-400 mt-2 text-sm">
            Faça login para gerenciar treinos e alunos
          </p>
        </div>

        <form onSubmit={handleLogin} className="space-y-5">
          
          {erro && (
            <div className="bg-red-500/10 border border-red-500/50 text-red-500 text-sm p-3 rounded-lg text-center">
              {erro}
            </div>
          )}

          <Input 
            label="E-mail" 
            type="email" 
            placeholder="admin@email.com" 
            value={email}
            onChange={(e: any) => setEmail(e.target.value)} 
          />
          
          <Input 
            label="Senha" 
            type="password" 
            placeholder="••••••••" 
            value={senha}
            onChange={(e: any) => setSenha(e.target.value)} 
          />

          <Button type="submit">
            Entrar no Sistema
          </Button>
        </form>

      </div>
    </main>
  );
}