"use client";

import { useEffect, useState } from "react";
import Link from "next/link";

interface Aluno {
  id: number;
  nome: string;
  email: string;
  modalidade: string;
  objetivo: string;
  ativo: boolean;
}

export default function AlunosPage() {
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    buscarAlunos();
  }, []);

  const buscarAlunos = async () => {
    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.trim().startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1].trim() : null;

      if (!token) {
        console.error("Token não encontrado");
        alert("Faça login novamente.");
        return;
      }

      console.log("Token enviado:", token.substring(0, 30) + "...");

      let resposta = await fetch("http://localhost:8080/api/alunos", {
        method: "GET",
        credentials: "include",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      console.log("Status admin:", resposta.status);

      if (resposta.status === 403 || resposta.status === 401 || resposta.status === 500) {
        console.log("Admin falhou, tentando personal...");
        resposta = await fetch("http://localhost:8080/api/personais/me/alunos", {
          method: "GET",
          credentials: "include",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        console.log("Status personal:", resposta.status);
      }

      if (resposta.ok) {
        const data = await resposta.json();
        console.log("Dados OK:", data);
        setAlunos(data);
      } else {
        const text = await resposta.text(); // Lê como texto primeiro
        let errorMsg = `Erro ${resposta.status}`;
        try {
          const errorData = JSON.parse(text);
          errorMsg += ` - ${errorData.message || errorData.error || JSON.stringify(errorData)}`;
        } catch {
          errorMsg += ` - ${text.substring(0, 200)}...`; // Mostra parte do corpo
        }
        console.error(errorMsg);
        alert(errorMsg);
      }
    } catch (error) {
      console.error("Erro de fetch:", error);
      alert("Erro de conexão.");
    } finally {
      setCarregando(false);
    }
  };

  const vincularPersonal = async (alunoId: number) => {
    // 1. Pergunta para qual Personal vamos enviar este aluno
    const personalId = prompt("Digite o ID numérico do Personal que vai treinar este aluno:");

    // Se o usuário clicar em Cancelar ou não digitar nada, a função para aqui
    if (!personalId) return;

    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1] : null;

      // 2. Envia para o Java com o ID que o usuário digitou!
      const resposta = await fetch(`http://localhost:8080/api/alunos/${alunoId}/vincular/${personalId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (resposta.ok) {
        alert("Aluno vinculado com sucesso!");
        buscarAlunos();
      } else {
        const dadosErro = await resposta.json();
        alert(`O Java bloqueou! Motivo: ${dadosErro.message || dadosErro.error || "Desconhecido"}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão ao tentar vincular.");
    }
  };

  // A FUNÇÃO DA FAXINA 🧹
  const handleExcluirAluno = async (id: number) => {
    if (!confirm("⚠️ ATENÇÃO: Tem certeza que deseja apagar DEFINITIVAMENTE este aluno do sistema?")) return;

    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1] : null;

      const resposta = await fetch(`http://localhost:8080/api/alunos/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      // Se o Personal tentar apagar, o sistema avisa!
      if (resposta.status === 403) {
        alert("Ação não permitida! Apenas o Administrador do sistema pode apagar um aluno definitivamente.");
        return;
      }

      if (resposta.ok) {
        buscarAlunos(); // Atualiza a tabela fazendo o aluno sumir!
      } else {
        alert("Erro ao tentar excluir o aluno do banco de dados.");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão ao tentar excluir.");
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Alunos</h1>
          <p className="text-zinc-400 mt-1">Gerencie os alunos cadastrados no sistema.</p>
        </div>
        <Link
          href="/dashboard/alunos/novo"
          className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-lg shadow-emerald-900/20"
        >
          + Novo Aluno
        </Link>
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl">
        {carregando ? (
          <p className="p-6 text-zinc-400 text-center">Buscando alunos no banco de dados...</p>
        ) : alunos.length === 0 ? (
          <p className="p-6 text-zinc-400 text-center">Nenhum aluno encontrado no sistema.</p>
        ) : (
          <table className="w-full text-left text-sm text-zinc-300">
            <thead className="bg-zinc-950/50 text-zinc-400 border-b border-zinc-800">
              <tr>
                <th className="px-6 py-4 font-medium">Nome</th>
                <th className="px-6 py-4 font-medium">E-mail</th>
                <th className="px-6 py-4 font-medium">Modalidade</th>
                <th className="px-6 py-4 font-medium">Objetivo</th>
                <th className="px-6 py-4 font-medium text-center">Ações & Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-800">
              {alunos.map((aluno) => (
                <tr key={aluno.id} className="hover:bg-zinc-800/50 transition-colors">
                  <td className="px-6 py-4 font-medium text-zinc-100">{aluno.nome}</td>
                  <td className="px-6 py-4">{aluno.email}</td>
                  <td className="px-6 py-4 capitalize">{aluno.modalidade}</td>
                  <td className="px-6 py-4 capitalize">{aluno.objetivo}</td>

                  <td className="px-6 py-4 text-center flex items-center justify-center gap-2">
                    <span className={`px-3 py-1.5 rounded-md text-xs font-medium ${aluno.ativo ? 'bg-emerald-500/10 text-emerald-500 border border-emerald-500/20' : 'bg-zinc-500/10 text-zinc-500 border border-zinc-500/20'}`}>
                      {aluno.ativo ? "Ativo" : "Inativo"}
                    </span>

                    {!aluno.ativo && (
                      <button
                        onClick={() => vincularPersonal(aluno.id)}
                        className="text-xs bg-blue-600/10 text-blue-400 border border-blue-600/30 hover:bg-blue-600 hover:text-white px-3 py-1.5 rounded-md transition-colors"
                      >
                        Vincular
                      </button>
                    )}

                    {/* BOTÃO DE APAGAR ADICIONADO AQUI */}
                    <button
                      onClick={() => handleExcluirAluno(aluno.id)}
                      className="text-xs bg-red-500/10 text-red-500 border border-red-500/20 hover:bg-red-500 hover:text-white px-3 py-1.5 rounded-md transition-colors"
                    >
                      Apagar
                    </button>
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