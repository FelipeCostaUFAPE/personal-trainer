"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Input } from "@/components/Input";
import { Button } from "@/components/Button";

interface Aluno {
  id: number;
  nome: string;
  ativo: boolean;
}

export default function NovoPlano() {
  const router = useRouter();
  const [erro, setErro] = useState("");
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [carregando, setCarregando] = useState(true);

  const [alunoId, setAlunoId] = useState("");
  const [nome, setNome] = useState("");
  const [dataInicio, setDataInicio] = useState("");
  const [dataFim, setDataFim] = useState("");

  // FUNÇÃO DE SEGURANÇA: Se o token vencer, expulsa pro Login!
  const lidarComErro401 = (status: number) => {
    if (status === 401) {
      document.cookie = "token=; path=/; max-age=0"; // Apaga o token vencido
      alert("Sua sessão expirou por segurança. Faça login novamente.");
      router.push("/"); // Expulsa pra tela inicial
      return true;
    }
    return false;
  };

  useEffect(() => {
    buscarAlunos();
  }, []);

  const buscarAlunos = async () => {
    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1] : null;

      if (!token) return;

      // 1. Tenta a rota do ADMIN primeiro
      let resposta = await fetch("http://localhost:8080/api/alunos", {
        headers: { Authorization: `Bearer ${token}` },
      });

      // 2. Se o Java disser "403 Proibido", o PERSONAL está logado!
      if (resposta.status === 403) {
        resposta = await fetch("http://localhost:8080/api/personais/me/alunos", {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      if (resposta.ok) {
        setAlunos(await resposta.json());
      } else {
        console.error("Erro ao buscar alunos. Status:", resposta.status);
      }
    } catch (error) {
      console.error(error);
    } finally {
      // ESTA É A LINHA QUE DESTRAVA A TABELA!
      if (typeof setCarregando === 'function') {
        setCarregando(false);
      }
    }
  };

  const handleCadastrar = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro("");

    if (!alunoId) {
      setErro("Por favor, selecione um aluno na lista.");
      return;
    }

    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1] : null;

      const formatarData = (dataOriginal: string) => {
        const [ano, mes, dia] = dataOriginal.split("-");
        return `${dia}/${mes}/${ano}`;
      };

      const resposta = await fetch("http://localhost:8080/api/planos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          alunoId: Number(alunoId),
          nome,
          dataInicio: formatarData(dataInicio),
          dataFim: formatarData(dataFim)
        }),
      });

      // O segurança de novo na hora de salvar!
      if (lidarComErro401(resposta.status)) return;

      if (resposta.ok) {
        router.push("/dashboard/treinos");
      } else {
        const dadosErro = await resposta.json();
        const mensagemErro = dadosErro.detail 
          ? Object.values(dadosErro.detail).join(", ") 
          : dadosErro.message || "Erro ao criar o plano de treino.";
        setErro(mensagemErro);
      }
    } catch (error) {
      console.error(error);
      setErro("Erro de conexão. O backend está rodando?");
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <div className="flex items-center gap-4 mb-8">
        <Link href="/dashboard/treinos" className="text-zinc-400 hover:text-zinc-100 transition-colors">
          ← Voltar
        </Link>
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Novo Plano de Treino</h1>
          <p className="text-zinc-400 mt-1">Crie a ficha base para um de seus alunos.</p>
        </div>
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-8 shadow-xl">
        <form onSubmit={handleCadastrar} className="space-y-6">
          
          {erro && (
            <div className="bg-red-500/10 border border-red-500/50 text-red-500 text-sm p-4 rounded-lg">
              {erro}
            </div>
          )}

          <div className="grid grid-cols-2 gap-6">
            
            <div className="col-span-2">
              <label className="block text-sm font-medium text-zinc-300 mb-1.5">Selecionar Aluno</label>
              <select 
                value={alunoId} 
                onChange={(e) => setAlunoId(e.target.value)}
                required
                className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 focus:border-emerald-500 transition-all"
              >
                <option value="" disabled>Escolha um aluno...</option>
                {alunos.map(aluno => (
                  <option key={aluno.id} value={aluno.id}>{aluno.nome}</option>
                ))}
              </select>
            </div>

            <div className="col-span-2">
              <Input label="Nome do Plano (Ex: Ficha A/B)" type="text" placeholder="Ficha de Hipertrofia - Verão" value={nome} onChange={(e: any) => setNome(e.target.value)} required />
            </div>

            <div className="col-span-2 md:col-span-1">
              <Input label="Data de Início" type="date" value={dataInicio} onChange={(e: any) => setDataInicio(e.target.value)} required />
            </div>

            <div className="col-span-2 md:col-span-1">
              <Input label="Data de Fim (Validade)" type="date" value={dataFim} onChange={(e: any) => setDataFim(e.target.value)} required />
            </div>

          </div>

          <div className="pt-4 flex justify-end">
            <Button type="submit">Criar Ficha</Button>
          </div>
        </form>
      </div>
    </div>
  );
}