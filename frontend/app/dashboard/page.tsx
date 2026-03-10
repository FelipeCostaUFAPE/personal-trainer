"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

// Moldes de Dados para o Dashboard
interface Aluno {
  id: number;
  ativo: boolean;
}
interface Fatura {
  id: number;
  valor: number;
  status: string;
}

export default function Dashboard() {
  const router = useRouter();

  const [totalAtivos, setTotalAtivos] = useState(0);
  const [receitaMensal, setReceitaMensal] = useState(0);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    const carregarDashboard = async () => {
      setCarregando(true);

      // Buscar alunos ativos (com fallback para personal)
      try {
        const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
        if (!token) return;

        let resposta = await fetch("http://localhost:8080/api/alunos", {
          headers: { Authorization: `Bearer ${token}` },
        });

        if (resposta.status === 403 || resposta.status === 401 || resposta.status === 500) {
          resposta = await fetch("http://localhost:8080/api/personais/me/alunos", {
            headers: { Authorization: `Bearer ${token}` },
          });
        }

        if (resposta.ok) {
          const dados: Aluno[] = await resposta.json();
          const ativos = dados.filter((aluno) => aluno.ativo).length;
          setTotalAtivos(ativos);
        }
      } catch (error) {
        console.error("Erro ao buscar alunos no dashboard:", error);
      }

      // Buscar receita (com fallback para personal)
      try {
        const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
        if (!token) return;

        let resposta = await fetch("http://localhost:8080/api/faturas", {
          headers: { Authorization: `Bearer ${token}` },
        });

        // Fallback para personal usando /api/faturas/me
        if (resposta.status === 403 || resposta.status === 401 || resposta.status === 500) {
          console.log("Rota faturas admin falhou, tentando /me...");
          resposta = await fetch("http://localhost:8080/api/faturas/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
        }

        if (resposta.ok) {
          const dados: Fatura[] = await resposta.json();
          const totalReceita = dados
            .filter(fatura => fatura.status?.toUpperCase() === "PAGA")
            .reduce((soma, fatura) => soma + (fatura.valor || 0), 0);
          setReceitaMensal(totalReceita);
        } else {
          console.error("Falha ao buscar faturas no dashboard. Status:", resposta.status);
        }
      } catch (error) {
        console.error("Erro ao buscar faturas no dashboard:", error);
      }

      setCarregando(false);
    };

    carregarDashboard();
  }, []); // Array vazio → roda uma vez só

  return (
    <div>
      <h1 className="text-3xl font-bold text-zinc-100">Visão Geral</h1>
      <p className="text-zinc-400 mt-2">Bem-vindo ao seu painel de controlo, Personal! 🎉</p>

      {/* Cards de Resumo */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
        {/* Card de Alunos */}
        <div className="bg-zinc-900 border border-zinc-800 p-6 rounded-xl shadow-lg transition-transform hover:scale-105 hover:border-emerald-500/50 cursor-pointer">
          <h3 className="text-zinc-400 text-sm font-medium mb-2">Alunos Ativos</h3>
          {carregando ? (
            <div className="h-10 w-16 bg-zinc-800 animate-pulse rounded-md"></div>
          ) : (
            <p className="text-4xl font-bold text-emerald-500">{totalAtivos}</p>
          )}
        </div>

        {/* Card de Receita Mensal */}
        <div className="bg-zinc-900 border border-zinc-800 p-6 rounded-xl shadow-lg transition-transform hover:scale-105 hover:border-blue-500/50 cursor-pointer">
          <h3 className="text-zinc-400 text-sm font-medium mb-2">Receita (Faturas Pagas)</h3>
          {carregando ? (
            <div className="h-10 w-32 bg-zinc-800 animate-pulse rounded-md"></div>
          ) : (
            <p className="text-4xl font-bold text-blue-500">
              R$ {receitaMensal.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
            </p>
          )}
        </div>
      </div>
    </div>
  );
}