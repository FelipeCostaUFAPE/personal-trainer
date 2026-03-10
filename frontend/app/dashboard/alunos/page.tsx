"use client";
import { useEffect, useState } from "react";
import Link from "next/link";

interface Aluno {
  id: number;
  nome: string;
  email: string;
  dataNascimento?: string;
  modalidade: string;
  objetivo: string;
  ativo: boolean;
  personalId?: number;
}

interface Personal {
  id: number;
  nome: string;
  cref?: string;
}

export default function AlunosPage() {
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [personais, setPersonais] = useState<Personal[]>([]);
  const [carregando, setCarregando] = useState(true);

  // Modal de edição
  const [modalEditarAberto, setModalEditarAberto] = useState(false);
  const [alunoEditandoId, setAlunoEditandoId] = useState<number | null>(null);
  const [nomeEdit, setNomeEdit] = useState("");
  const [dataNascimentoEdit, setDataNascimentoEdit] = useState("");
  const [modalidadeEdit, setModalidadeEdit] = useState("");
  const [objetivoEdit, setObjetivoEdit] = useState("");
  const [salvando, setSalvando] = useState(false);

  // Modal de vinculação para admin
  const [modalVincularAberto, setModalVincularAberto] = useState(false);
  const [alunoVinculandoId, setAlunoVinculandoId] = useState<number | null>(null);
  const [personalSelecionadoId, setPersonalSelecionadoId] = useState("");

  useEffect(() => {
    buscarAlunos();
    buscarPersonais();
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

      let resposta = await fetch("http://localhost:8080/api/alunos", {
        method: "GET",
        credentials: "include",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

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
      }

      if (resposta.ok) {
        const data = await resposta.json();
        setAlunos(data);
      } else {
        const text = await resposta.text();
        let errorMsg = `Erro ${resposta.status}`;
        try {
          const errorData = JSON.parse(text);
          errorMsg += ` - ${errorData.message || errorData.error || JSON.stringify(errorData)}`;
        } catch {
          errorMsg += ` - ${text.substring(0, 200)}...`;
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

  const buscarPersonais = async () => {
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
      const resposta = await fetch("http://localhost:8080/api/personais", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (resposta.ok) {
        setPersonais(await resposta.json());
      }
    } catch (error) {
      console.error("Erro ao buscar personais:", error);
    }
  };

  const vincularPersonal = async (alunoId: number) => {
    setAlunoVinculandoId(alunoId);
    setPersonalSelecionadoId("");
    setModalVincularAberto(true);
  };

  const handleVincularConfirmar = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!personalSelecionadoId) {
      alert("Selecione um personal.");
      return;
    }

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
      const resposta = await fetch(`http://localhost:8080/api/alunos/${alunoVinculandoId}/vincular/${personalSelecionadoId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (resposta.ok) {
        alert("Aluno vinculado com sucesso!");
        setModalVincularAberto(false);
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

  const handleDesvincularAluno = async (alunoId: number) => {
    if (!confirm("Tem certeza que deseja desvincular este aluno do personal?")) return;

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
      const resposta = await fetch(`http://localhost:8080/api/alunos/${alunoId}/desvincular`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      });

      if (resposta.ok) {
        alert("Aluno desvinculado com sucesso!");
        buscarAlunos();
      } else {
        const dadosErro = await resposta.json();
        alert(`Erro ao desvincular: ${dadosErro.message || dadosErro.error || "Desconhecido"}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão ao tentar desvincular.");
    }
  };

  const handleExcluirAluno = async (id: number) => {
    if (!confirm("⚠️ ATENÇÃO: Tem certeza que deseja apagar DEFINITIVAMENTE este aluno do sistema?")) return;

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
      const resposta = await fetch(`http://localhost:8080/api/alunos/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (resposta.status === 403) {
        alert("Ação não permitida! Apenas o Administrador do sistema pode apagar um aluno definitivamente.");
        return;
      }

      if (resposta.ok) {
        buscarAlunos();
      } else {
        alert("Erro ao tentar excluir o aluno do banco de dados.");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão ao tentar excluir.");
    }
  };

  const abrirModalEditar = (aluno: Aluno) => {
    setAlunoEditandoId(aluno.id);
    setNomeEdit(aluno.nome);
    setDataNascimentoEdit(aluno.dataNascimento ? aluno.dataNascimento.split("T")[0] : "");
    setModalidadeEdit(aluno.modalidade || "");
    setObjetivoEdit(aluno.objetivo || "");
    setModalEditarAberto(true);
  };

  const handleEditarAluno = async (e: React.FormEvent) => {
    e.preventDefault();
    setSalvando(true);

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();

      const formatarData = (data: string) => {
        if (!data) return null;
        const [ano, mes, dia] = data.split("-");
        return `${dia}/${mes}/${ano}`;
      };

      const resposta = await fetch(`http://localhost:8080/api/alunos/${alunoEditandoId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          nome: nomeEdit,
          dataNascimento: formatarData(dataNascimentoEdit),
          modalidade: modalidadeEdit,
          objetivo: objetivoEdit,
        }),
      });

      if (resposta.ok) {
        setModalEditarAberto(false);
        setAlunoEditandoId(null);
        setNomeEdit("");
        setDataNascimentoEdit("");
        setModalidadeEdit("");
        setObjetivoEdit("");
        buscarAlunos();
      } else {
        const erro = await resposta.json();
        alert(`Erro ao editar aluno: ${erro.message || JSON.stringify(erro)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão.");
    } finally {
      setSalvando(false);
    }
  };

  const formatarDataNascimento = (data?: string) => {
    if (!data || data.trim() === "" || data === "undefined") return "-";
    const partes = data.split("/");
    if (partes.length === 3) {
      const [dia, mes, ano] = partes;
      // Validação simples: dia/mês/ano numéricos e com comprimento correto
      if (
        dia.length === 2 && mes.length === 2 && ano.length === 4 &&
        !isNaN(Number(dia)) && !isNaN(Number(mes)) && !isNaN(Number(ano))
      ) {
        return data;
      }
    }

    // Se não for dd/MM/yyyy válido, tenta tratar como ISO (yyyy-MM-dd)
    const dataLimpa = data.split("T")[0].trim();
    const partesIso = dataLimpa.split("-");
    if (partesIso.length === 3) {
      const [ano, mes, dia] = partesIso;
      return `${dia}/${mes}/${ano}`;
    }

    return "-";
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
                <th className="px-6 py-4 font-medium">Data Nasc.</th>
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
                  <td className="px-6 py-4">{formatarDataNascimento(aluno.dataNascimento)}</td>
                  <td className="px-6 py-4 capitalize">{aluno.modalidade}</td>
                  <td className="px-6 py-4 capitalize">{aluno.objetivo}</td>
                  <td className="px-6 py-4 text-center flex items-center justify-center gap-2 flex-wrap">
                    <span className={`px-3 py-1.5 rounded-md text-xs font-medium ${aluno.ativo ? 'bg-emerald-500/10 text-emerald-500 border border-emerald-500/20' : 'bg-zinc-500/10 text-zinc-500 border border-zinc-500/20'}`}>
                      {aluno.ativo ? "Ativo" : "Inativo"}
                    </span>

                    <button
                      onClick={() => abrirModalEditar(aluno)}
                      className="text-xs bg-emerald-600/10 text-emerald-400 border border-emerald-600/30 hover:bg-emerald-600 hover:text-white px-3 py-1.5 rounded-md transition-colors"
                    >
                      Editar
                    </button>

                    {aluno.personalId && (
                      <button
                        onClick={() => handleDesvincularAluno(aluno.id)}
                        className="text-xs bg-amber-600/10 text-amber-400 border border-amber-600/30 hover:bg-amber-600 hover:text-white px-3 py-1.5 rounded-md transition-colors"
                      >
                        Desvincular
                      </button>
                    )}

                    {!aluno.personalId && (
                      <button
                        onClick={() => vincularPersonal(aluno.id)}
                        className="text-xs bg-blue-600/10 text-blue-400 border border-blue-600/30 hover:bg-blue-600 hover:text-white px-3 py-1.5 rounded-md transition-colors"
                      >
                        Vincular
                      </button>
                    )}

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

      {/* Modal Editar Aluno */}
      {modalEditarAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-2xl font-bold text-zinc-100 mb-6">Editar Aluno</h2>
            <form onSubmit={handleEditarAluno} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Nome</label>
                <input
                  type="text"
                  value={nomeEdit}
                  onChange={(e) => setNomeEdit(e.target.value)}
                  required
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Data de Nascimento</label>
                <input
                  type="date"
                  value={dataNascimentoEdit}
                  onChange={(e) => setDataNascimentoEdit(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Modalidade</label>
                <select
                  value={modalidadeEdit}
                  onChange={(e) => setModalidadeEdit(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                >
                  <option value="">Selecione...</option>
                  <option value="presencial">Presencial</option>
                  <option value="online">Online</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Objetivo</label>
                <input
                  type="text"
                  value={objetivoEdit}
                  onChange={(e) => setObjetivoEdit(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setModalEditarAberto(false)}
                  className="px-4 py-2.5 text-zinc-400 hover:text-zinc-100 font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={salvando}
                  className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium shadow-lg disabled:opacity-50"
                >
                  {salvando ? "Salvando..." : "Salvar Alterações"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal Vincular Aluno (para admin) */}
      {modalVincularAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-2xl font-bold text-zinc-100 mb-6">Vincular Aluno a Personal</h2>
            <form onSubmit={handleVincularConfirmar} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Selecione o Personal</label>
                <select
                  value={personalSelecionadoId}
                  onChange={(e) => setPersonalSelecionadoId(e.target.value)}
                  required
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                >
                  <option value="" disabled>Escolha um personal...</option>
                  {personais.map((personal) => (
                    <option key={personal.id} value={personal.id}>
                      {personal.nome} {personal.cref ? `(CREF: ${personal.cref})` : ""}
                    </option>
                  ))}
                </select>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setModalVincularAberto(false)}
                  className="px-4 py-2.5 text-zinc-400 hover:text-zinc-100 font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium shadow-lg"
                >
                  Vincular Aluno
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}