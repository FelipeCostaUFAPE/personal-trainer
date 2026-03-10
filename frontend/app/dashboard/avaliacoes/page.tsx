"use client";
import { useEffect, useState } from "react";

interface Aluno {
  id: number;
  nome: string;
}

interface Avaliacao {
  avaliacaoId: number;
  alunoId: number;
  alunoNome: string;
  dataAvaliacao: string;
  pesoKg: number;
  alturaCm: number;
  percentualGordura: number;
  observacoes?: string;
  feitoPeloPersonal: boolean;
}

export default function AvaliacoesPage() {
  const [avaliacoes, setAvaliacoes] = useState<Avaliacao[]>([]);
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [carregando, setCarregando] = useState(true);

  // Modal nova avaliação
  const [modalAberto, setModalAberto] = useState(false);
  const [salvando, setSalvando] = useState(false);

  const [alunoSelecionadoId, setAlunoSelecionadoId] = useState("");
  const [dataAvaliacao, setDataAvaliacao] = useState("");
  const [pesoKg, setPesoKg] = useState("");
  const [alturaCm, setAlturaCm] = useState("");
  const [percentualGordura, setPercentualGordura] = useState("");
  const [observacoes, setObservacoes] = useState("");

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    setCarregando(true);
    await buscarAlunos();
    await buscarAvaliacoes();
    setCarregando(false);
  };

  const buscarAlunos = async () => {
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
        setAlunos(await resposta.json());
      }
    } catch (error) {
      console.error("Erro ao buscar alunos:", error);
    }
  };

  const buscarAvaliacoes = async () => {
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
      if (!token) return;

      let resposta = await fetch("http://localhost:8080/api/avaliacoes", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (resposta.status === 403 || resposta.status === 401 || resposta.status === 500) {
        console.log("Rota principal falhou, tentando /me para personal...");
        resposta = await fetch("http://localhost:8080/api/avaliacoes/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      if (resposta.ok) {
        setAvaliacoes(await resposta.json());
      } else {
        console.error("Falha ao buscar avaliações. Status:", resposta.status);
      }
    } catch (error) {
      console.error("Erro ao buscar avaliações:", error);
    }
  };

  const handleCriarAvaliacao = async (e: React.FormEvent) => {
    e.preventDefault();
    setSalvando(true);

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();

      const formatarData = (data: string) => {
        if (!data) return "";
        const [ano, mes, dia] = data.split("-");
        return `${dia}/${mes}/${ano}`;
      };

      const resposta = await fetch("http://localhost:8080/api/avaliacoes", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          alunoId: Number(alunoSelecionadoId),
          dataAvaliacao: formatarData(dataAvaliacao),
          pesoKg: Number(pesoKg),
          alturaCm: Number(alturaCm),
          percentualGordura: Number(percentualGordura),
          observacoes,
        }),
      });

      if (resposta.ok) {
        setModalAberto(false);
        setAlunoSelecionadoId("");
        setDataAvaliacao("");
        setPesoKg("");
        setAlturaCm("");
        setPercentualGordura("");
        setObservacoes("");
        buscarAvaliacoes();
      } else {
        const erro = await resposta.json();
        alert(`Erro: ${erro.message || JSON.stringify(erro)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão.");
    } finally {
      setSalvando(false);
    }
  };

  const handleDeletarAvaliacao = async (id: number) => {
    if (!confirm("Tem certeza que deseja excluir esta avaliação?")) return;

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();

      const resposta = await fetch(`http://localhost:8080/api/avaliacoes/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (resposta.ok) {
        buscarAvaliacoes();
      } else {
        alert("Erro ao excluir avaliação.");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão.");
    }
  };

  const formatarDataExibicao = (data: string) => {
    if (!data) return "-";
    const [ano, mes, dia] = data.split("-");
    return `${dia}/${mes}/${ano}`;
  };

  const getStatusCor = (feitoPeloPersonal: boolean) => {
    return feitoPeloPersonal
      ? "bg-emerald-500/10 text-emerald-500 border-emerald-500/20"
      : "bg-blue-500/10 text-blue-500 border-blue-500/20";
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Avaliações Físicas</h1>
          <p className="text-zinc-400 mt-1">Registre e acompanhe a evolução física dos seus alunos.</p>
        </div>
        <button
          onClick={() => setModalAberto(true)}
          className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-lg shadow-emerald-900/20"
        >
          + Nova Avaliação
        </button>
      </div>

      {carregando ? (
        <div className="p-12 text-center text-zinc-400">Carregando avaliações...</div>
      ) : avaliacoes.length === 0 ? (
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-12 text-center shadow-xl">
          <span className="text-4xl mb-3 block">📏</span>
          <p className="text-zinc-400">Nenhuma avaliação cadastrada no sistema.</p>
          <p className="text-zinc-500 text-sm mt-1">Clique no botão verde acima para registrar a primeira.</p>
        </div>
      ) : (
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-zinc-300">
              <thead className="bg-zinc-950/50 text-zinc-500 border-b border-zinc-800">
                <tr>
                  <th className="px-6 py-4 font-medium">Aluno</th>
                  <th className="px-6 py-4 font-medium">Data</th>
                  <th className="px-6 py-4 font-medium">Peso (kg)</th>
                  <th className="px-6 py-4 font-medium">Altura (cm)</th>
                  <th className="px-6 py-4 font-medium">% Gordura</th>
                  <th className="px-6 py-4 font-medium">Quem fez</th>
                  <th className="px-6 py-4 font-medium">Observações</th>
                  <th className="px-6 py-4 font-medium text-right">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-800/50">
                {avaliacoes.map((av) => (
                  <tr key={av.avaliacaoId} className="hover:bg-zinc-800/30 transition-colors">
                    <td className="px-6 py-4 font-medium text-zinc-100">{av.alunoNome}</td>
                    <td className="px-6 py-4">{formatarDataExibicao(av.dataAvaliacao)}</td>
                    <td className="px-6 py-4 font-medium text-zinc-100">{av.pesoKg} kg</td>
                    <td className="px-6 py-4 font-medium text-zinc-100">{av.alturaCm} cm</td>
                    <td className="px-6 py-4 font-medium text-zinc-100">{av.percentualGordura}%</td>
                    <td className="px-6 py-4">
                      <span className={`px-3 py-1 text-xs font-bold rounded-full border ${getStatusCor(av.feitoPeloPersonal)}`}>
                        {av.feitoPeloPersonal ? "Personal" : "Aluno"}
                      </span>
                    </td>
                    <td className="px-6 py-4">{av.observacoes || "-"}</td>
                    <td className="px-6 py-4 text-right">
                      <button
                        onClick={() => handleDeletarAvaliacao(av.avaliacaoId)}
                        className="text-red-500 hover:text-red-400 text-xs font-medium bg-red-500/10 hover:bg-red-500/20 px-3 py-1.5 rounded transition-colors"
                      >
                        Apagar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Modal Nova Avaliação */}
      {modalAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-2xl font-bold text-zinc-100 mb-6">Nova Avaliação Física</h2>
            <form onSubmit={handleCriarAvaliacao} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Aluno</label>
                <select
                  value={alunoSelecionadoId}
                  onChange={(e) => setAlunoSelecionadoId(e.target.value)}
                  required
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                >
                  <option value="" disabled>Selecione o aluno...</option>
                  {alunos.map((aluno) => (
                    <option key={aluno.id} value={aluno.id}>
                      {aluno.nome}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Data da Avaliação</label>
                <input
                  type="date"
                  value={dataAvaliacao}
                  onChange={(e) => setDataAvaliacao(e.target.value)}
                  required
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-zinc-300 mb-2">Peso (kg)</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    value={pesoKg}
                    onChange={(e) => setPesoKg(e.target.value)}
                    required
                    className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-zinc-300 mb-2">Altura (cm)</label>
                  <input
                    type="number"
                    min="0"
                    value={alturaCm}
                    onChange={(e) => setAlturaCm(e.target.value)}
                    required
                    className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">% de Gordura</label>
                <input
                  type="number"
                  step="0.1"
                  min="0"
                  value={percentualGordura}
                  onChange={(e) => setPercentualGordura(e.target.value)}
                  required
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Observações</label>
                <textarea
                  value={observacoes}
                  onChange={(e) => setObservacoes(e.target.value)}
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 h-24 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                  placeholder="Detalhes adicionais..."
                />
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setModalAberto(false)}
                  className="px-4 py-2.5 text-zinc-400 hover:text-zinc-100 font-medium"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={salvando}
                  className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium shadow-lg disabled:opacity-50"
                >
                  {salvando ? "Salvando..." : "Registrar Avaliação"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}