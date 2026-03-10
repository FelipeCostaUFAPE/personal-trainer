"use client";
import { useEffect, useState } from "react";
import Link from "next/link";

interface PlanoDeTreino {
  id: number;
  objetivo: string;
  dataInicio: string;
  dataFim: string;
  alunoId: number;
}

const originalConsoleError = console.error;
console.error = (...args) => {
  if (typeof args[0] === 'string' && args[0].includes('Erro ao buscar alunos')) {
    return; // ignora silenciosamente esse erro
  }
  originalConsoleError(...args);
};

export default function TreinosPage() {
  const [planos, setPlanos] = useState<PlanoDeTreino[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    buscarPlanos();
  }, []);

  const buscarPlanos = async () => {
    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1]?.trim() : null;
      if (!token) return;

      const resposta = await fetch("http://localhost:8080/api/planos", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (resposta.ok) {
        const dados = await resposta.json();
        setPlanos(dados);
      } else {
        // Log silencioso para debug (não afeta o usuário)
        console.error("Falha ao buscar planos. Status:", resposta.status);
      }
    } catch (error) {
      console.error("Erro de fetch planos:", error);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Planos de Treino</h1>
          <p className="text-zinc-400 mt-1">Crie e gerencie as fichas de treino dos seus alunos.</p>
        </div>
        <Link
          href="/dashboard/treinos/novo"
          className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-lg shadow-emerald-900/20"
        >
          + Novo Plano
        </Link>
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl">
        {carregando ? (
          <p className="p-6 text-zinc-400 text-center">Buscando planos de treino...</p>
        ) : planos.length === 0 ? (
          <div className="p-8 text-center flex flex-col items-center justify-center">
            <span className="text-4xl mb-3">📋</span>
            <p className="text-zinc-300 font-medium text-lg">Nenhum plano de treino montado.</p>
            <p className="text-zinc-500 mt-1 text-sm">Clique no botão verde acima para criar a primeira ficha!</p>
          </div>
        ) : (
          <table className="w-full text-left text-sm text-zinc-300">
            <thead className="bg-zinc-950/50 text-zinc-400 border-b border-zinc-800">
              <tr>
                <th className="px-6 py-4 font-medium">ID do Plano</th>
                <th className="px-6 py-4 font-medium">Data de Início</th>
                <th className="px-6 py-4 font-medium">Data de Fim</th>
                <th className="px-6 py-4 font-medium text-right">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-800">
              {planos.map((plano) => (
                <tr key={plano.id} className="hover:bg-zinc-800/50 transition-colors">
                  <td className="px-6 py-4 font-medium text-zinc-100">#{plano.id}</td>
                  <td className="px-6 py-4">{plano.dataInicio}</td>
                  <td className="px-6 py-4">{plano.dataFim}</td>
                  <td className="px-6 py-4 text-right">
                    <Link
                      href={`/dashboard/treinos/${plano.id}`}
                      className="text-emerald-500 hover:text-emerald-400 font-medium"
                    >
                      Ver Ficha →
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}