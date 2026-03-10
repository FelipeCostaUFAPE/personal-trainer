"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { Input } from "@/components/Input";
import { Button } from "@/components/Button";

export default function NovoAluno() {
  const router = useRouter();
  const [erro, setErro] = useState("");

  // === ESTADOS DO ALUNO ===
  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [dataNascimento, setDataNascimento] = useState("");
  const [modalidade, setModalidade] = useState("presencial");
  const [objetivo, setObjetivo] = useState("");

  // === ESTADOS DE VINCULAÇÃO ===
  const [isAdmin, setIsAdmin] = useState(false);
  const [personais, setPersonais] = useState<any[]>([]);
  const [personalSelecionado, setPersonalSelecionado] = useState(""); // Usado só pelo Admin
  const [cadastrando, setCadastrando] = useState(false);

  useEffect(() => {
    verificarPermissaoEBuscarPersonais();
  }, []);

  const verificarPermissaoEBuscarPersonais = async () => {
    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1] : null;

      if (!token) return;

      const resposta = await fetch("http://localhost:8080/api/personais", {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (resposta.ok) {
        setIsAdmin(true);
        setPersonais(await resposta.json());
      } else {
        setIsAdmin(false); // É um Personal! A tela esconde as opções de vínculo.
      }
    } catch (error) {
      console.error("Erro ao verificar permissões:", error);
    }
  };

  const handleCadastrar = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro("");
    setCadastrando(true);

    try {
      const cookies = document.cookie.split("; ");
      const tokenCookie = cookies.find((row) => row.startsWith("token="));
      const token = tokenCookie ? tokenCookie.split("=")[1] : null;

      const [ano, mes, dia] = dataNascimento.split("-");
      const dataFormatada = `${dia}/${mes}/${ano}`;

      // 1. CRIA O ALUNO 
      const respostaAluno = await fetch("http://localhost:8080/api/alunos", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          nome, email, senha, dataNascimento: dataFormatada, modalidade, objetivo,
        }),
      });

      if (respostaAluno.ok) {
        const dadosAlunoCriado = await respostaAluno.json(); 
        
        let idParaVincular = null;

        // 2. DESCOBRE O ID DO TREINADOR
        if (isAdmin) {
          idParaVincular = personalSelecionado; // Pega do Dropdown
        } else {
          // A MÁGICA: Bate na nossa nova rota do Java para descobrir quem é o Personal logado!
          const respostaMe = await fetch("http://localhost:8080/api/personais/me", {
            headers: { Authorization: `Bearer ${token}` }
          });
          
          if (respostaMe.ok) {
            const meuPerfil = await respostaMe.json();
            idParaVincular = meuPerfil.id;
          }
        }

        // 3. FAZ O VÍNCULO AUTOMÁTICO
        if (idParaVincular) {
          await fetch(`http://localhost:8080/api/alunos/${dadosAlunoCriado.id}/vincular/${idParaVincular}`, {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${token}`,
            },
          });
        }

        // Sucesso total! Volta para a listagem
        router.push("/dashboard/alunos");

      } else {
        const dadosErro = await respostaAluno.json();
        const mensagemErro = dadosErro.detail 
          ? Object.values(dadosErro.detail).join(", ") 
          : "Erro ao cadastrar aluno.";
        setErro(mensagemErro);
      }
    } catch (error) {
      console.error(error);
      setErro("Erro de conexão. Verifique se o backend está rodando.");
    } finally {
      setCadastrando(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <div className="flex items-center gap-4 mb-8">
        <Link href="/dashboard/alunos" className="text-zinc-400 hover:text-zinc-100 transition-colors">
          ← Voltar
        </Link>
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Novo Aluno</h1>
          <p className="text-zinc-400 mt-1">Preencha os dados para cadastrar um novo aluno.</p>
        </div>
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-8 shadow-xl">
        <form onSubmit={handleCadastrar} className="space-y-6">
          
          {erro && (
            <div className="bg-red-500/10 border border-red-500/50 text-red-500 text-sm p-4 rounded-lg">
              {erro}
            </div>
          )}

          {/* SÓ MOSTRA SE FOR ADMIN */}
          {isAdmin && (
            <div className="bg-zinc-950/50 p-6 rounded-lg border border-zinc-800/50 mb-6">
              <h3 className="text-emerald-500 font-medium mb-4 flex items-center gap-2">
                <span>🔗</span> Vínculo com o Treinador
              </h3>
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-1.5">Selecione o Personal (Opcional)</label>
                <select 
                  value={personalSelecionado} 
                  onChange={(e) => setPersonalSelecionado(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                >
                  <option value="">Deixar aluno sem vínculo por enquanto</option>
                  {personais.map(p => (
                    <option key={p.id} value={p.id}>{p.nome} (CREF: {p.cref})</option>
                  ))}
                </select>
              </div>
            </div>
          )}

          <div className="grid grid-cols-2 gap-6 pt-2">
            <div className="col-span-2">
              <Input label="Nome Completo" type="text" placeholder="João da Silva" value={nome} onChange={(e: any) => setNome(e.target.value)} required />
            </div>
            
            <div className="col-span-2 md:col-span-1">
              <Input label="E-mail" type="email" placeholder="joao@email.com" value={email} onChange={(e: any) => setEmail(e.target.value)} required />
            </div>

            <div className="col-span-2 md:col-span-1">
              <Input label="Senha Provisória" type="password" placeholder="••••••••" value={senha} onChange={(e: any) => setSenha(e.target.value)} required minLength={4} />
            </div>

            <div className="col-span-2 md:col-span-1">
              <Input label="Data de Nascimento" type="date" value={dataNascimento} onChange={(e: any) => setDataNascimento(e.target.value)} required />
            </div>

            <div className="col-span-2 md:col-span-1">
              <Input label="Objetivo" type="text" placeholder="Ex: Hipertrofia, Emagrecimento..." value={objetivo} onChange={(e: any) => setObjetivo(e.target.value)} required />
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-zinc-300 mb-1.5">Modalidade</label>
              <select 
                value={modalidade} 
                onChange={(e) => setModalidade(e.target.value)}
                className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50 transition-all"
              >
                <option value="presencial">Presencial</option>
                <option value="online">Online</option>
              </select>
            </div>
          </div>

          <div className="pt-4 flex justify-end">
            <Button type="submit" disabled={cadastrando}>
              {cadastrando ? "Cadastrando e Vinculando..." : "Cadastrar Aluno"}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}