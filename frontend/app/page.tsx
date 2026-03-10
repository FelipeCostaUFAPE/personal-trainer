"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();

  // O "Interruptor" que troca entre a tela de Login e a de Cadastro
  const [isLogin, setIsLogin] = useState(true);
  const [carregando, setCarregando] = useState(false);

  // === CAMPOS DE LOGIN ===
  const [emailLogin, setEmailLogin] = useState("");
  const [senhaLogin, setSenhaLogin] = useState("");

  // === CAMPOS DE CADASTRO (PERSONAL) ===
  const [nomeReg, setNomeReg] = useState("");
  const [emailReg, setEmailReg] = useState("");
  const [senhaReg, setSenhaReg] = useState("");
  const [crefReg, setCrefReg] = useState("");

  // Função de Entrar
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setCarregando(true);

    try {
      const resposta = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: emailLogin, senha: senhaLogin }),
      });

      if (resposta.ok) {
        const dados = await resposta.json();
        // Guarda o token e entra no sistema
        document.cookie = `token=${dados.token}; path=/; max-age=86400`;
        router.push("/dashboard");
      } else {
        alert("E-mail ou senha incorretos!");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão com o servidor.");
    } finally {
      setCarregando(false);
    }
  };

  // Função de Cadastrar
  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setCarregando(true);

    try {
      // ATENÇÃO: Estou usando a rota padrão /api/personais
      const resposta = await fetch("http://localhost:8080/api/personais", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nome: nomeReg,
          email: emailReg,
          senha: senhaReg,
          // O toUpperCase() garante que o "sp" vire "SP" para o Java não reclamar!
          cref: crefReg.toUpperCase(), 
        }),
      });

      if (resposta.ok) {
        alert("Conta de Personal criada com sucesso! Faça login para começar.");
        setIsLogin(true); // Volta para a aba de login sozinnho!
        setNomeReg(""); setEmailReg(""); setSenhaReg(""); setCrefReg("");
      } else {
        const erroDetalhado = await resposta.json();
        alert(`O Java bloqueou! Motivo: ${erroDetalhado.message || erroDetalhado.error || JSON.stringify(erroDetalhado)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão com o servidor.");
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="min-h-screen bg-zinc-950 flex items-center justify-center p-4">
      <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
        
        {/* Cabeçalho */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-emerald-500 tracking-wider mb-2">
            Personal System
          </h1>
          <p className="text-zinc-400">
            {isLogin ? "Faça login para gerenciar seus alunos." : "Crie sua conta de Treinador."}
          </p>
        </div>

        {/* Abas de Navegação */}
        <div className="flex bg-zinc-950 rounded-lg p-1 mb-8">
          <button 
            onClick={() => setIsLogin(true)}
            className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${isLogin ? "bg-zinc-800 text-zinc-100" : "text-zinc-500 hover:text-zinc-300"}`}
          >
            Entrar
          </button>
          <button 
            onClick={() => setIsLogin(false)}
            className={`flex-1 py-2 rounded-md text-sm font-medium transition-colors ${!isLogin ? "bg-zinc-800 text-zinc-100" : "text-zinc-500 hover:text-zinc-300"}`}
          >
            Cadastrar
          </button>
        </div>

        {/* FORMULÁRIO DE LOGIN */}
        {isLogin ? (
          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <label className="block text-sm font-medium text-zinc-300 mb-2">E-mail</label>
              <input type="email" required value={emailLogin} onChange={e => setEmailLogin(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" placeholder="exemplo@gmail.com" />
            </div>
            <div>
              <label className="block text-sm font-medium text-zinc-300 mb-2">Senha</label>
              <input type="password" required value={senhaLogin} onChange={e => setSenhaLogin(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" placeholder="••••••••" />
            </div>
            <button type="submit" disabled={carregando} className="w-full bg-emerald-600 hover:bg-emerald-500 text-white py-3.5 rounded-lg font-bold shadow-lg transition-colors disabled:opacity-50 mt-2">
              {carregando ? "Entrando..." : "Acessar Sistema"}
            </button>
          </form>
        ) : (
          
          /* FORMULÁRIO DE CADASTRO */
          <form onSubmit={handleRegister} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-zinc-300 mb-2">Nome Completo</label>
              <input type="text" required value={nomeReg} onChange={e => setNomeReg(e.target.value)} minLength={4} maxLength={50} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" placeholder="João Silva" />
            </div>
            <div>
              <label className="block text-sm font-medium text-zinc-300 mb-2">E-mail Profissional</label>
              <input type="email" required value={emailReg} onChange={e => setEmailReg(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" placeholder="joao@personalsystem.com" />
            </div>
            <div>
              <label className="block text-sm font-medium text-zinc-300 mb-2">Registro CREF</label>
              <input type="text" required value={crefReg} onChange={e => setCrefReg(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 uppercase" placeholder="123456-SP" title="Formato exigido: 123456-SP" pattern="\d{6}-[A-Za-z]{2}" />
              <p className="text-xs text-zinc-500 mt-1">Siga o formato exato: 123456-SP</p>
            </div>
            <div>
              <label className="block text-sm font-medium text-zinc-300 mb-2">Senha</label>
              <input type="password" required value={senhaReg} onChange={e => setSenhaReg(e.target.value)} minLength={4} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" placeholder="••••••••" />
            </div>
            <button type="submit" disabled={carregando} className="w-full bg-zinc-100 hover:bg-white text-zinc-900 py-3.5 rounded-lg font-bold shadow-lg transition-colors disabled:opacity-50 mt-4">
              {carregando ? "Cadastrando..." : "Criar Minha Conta"}
            </button>
          </form>
        )}

      </div>
    </div>
  );
}