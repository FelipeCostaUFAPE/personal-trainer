"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

interface GrupoMuscular {
  id?: number;
  grupoMuscularId?: number; 
  nome: string;
}

interface Exercicio {
  id: number;
  nome: string;
  descricao?: string;
  grupoMuscular?: GrupoMuscular; 
}

export default function ExerciciosPage() {
  const router = useRouter();
  
  const [exercicios, setExercicios] = useState<Exercicio[]>([]);
  const [grupos, setGrupos] = useState<GrupoMuscular[]>([]);
  const [carregando, setCarregando] = useState(true);

  // Controle de acesso (botão Novo Grupo só para admin)
  const [isAdmin, setIsAdmin] = useState(false);

  // Estados dos Modais
  const [modalGrupoAberto, setModalGrupoAberto] = useState(false);
  const [modalExercicioAberto, setModalExercicioAberto] = useState(false);
  const [modalEditarAberto, setModalEditarAberto] = useState(false);

  // Campos dos formulários
  const [nomeGrupo, setNomeGrupo] = useState("");
  const [descricaoGrupo, setDescricaoGrupo] = useState("");
  const [nomeExercicio, setNomeExercicio] = useState("");
  const [descricaoExercicio, setDescricaoExercicio] = useState("");
  const [grupoSelecionadoId, setGrupoSelecionadoId] = useState("");

  // Campos edição
  const [exercicioEditandoId, setExercicioEditandoId] = useState<number | null>(null);
  const [nomeEdit, setNomeEdit] = useState("");
  const [descricaoEdit, setDescricaoEdit] = useState("");
  const [salvando, setSalvando] = useState(false);

  const lidarComErro401 = (status: number) => {
    if (status === 401) {
      document.cookie = "token=; path=/; max-age=0";
      alert("A sua sessão expirou por segurança. Faça login novamente.");
      router.push("/");
      return true;
    }
    return false;
  };

  // Verifica se é admin
  const verificarPermissaoAdmin = async () => {
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      if (!token) return;

      const resposta = await fetch("http://localhost:8080/api/personais", {
        headers: { Authorization: `Bearer ${token}` }
      });
      setIsAdmin(resposta.ok); 
    } catch (error) {
      console.error(error);
    }
  };

  const carregarDados = async () => {
    setCarregando(true);
    const gruposOk = await buscarGrupos();
    if (gruposOk) {
      await buscarExercicios();
    }
    setCarregando(false);
  };

  useEffect(() => {
    verificarPermissaoAdmin();
    carregarDados();
  }, [carregarDados]); // Dependência adicionada para remover aviso do React Hook

  const buscarGrupos = async () => {
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      if (!token) { lidarComErro401(401); return false; }

      const resposta = await fetch("http://localhost:8080/api/grupos", {
        headers: { Authorization: `Bearer ${token}` }
      });
      
      if (lidarComErro401(resposta.status)) return false;
      
      if (resposta.ok) {
        setGrupos(await resposta.json());
        return true;
      }
    } catch (error) {
      console.error(error);
    }
    return false;
  };

  const buscarExercicios = async () => {
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch("http://localhost:8080/api/exercicios", {
        headers: { Authorization: `Bearer ${token}` }
      });
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) setExercicios(await resposta.json());
    } catch (error) {
      console.error(error);
    }
  };

  const handleCriarGrupo = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      
      const resposta = await fetch("http://localhost:8080/api/grupos", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({ nome: nomeGrupo, descricao: descricaoGrupo }),
      });
      
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        setNomeGrupo(""); setDescricaoGrupo(""); 
        setModalGrupoAberto(false);
        buscarGrupos(); 
      } else {
        const erroDetalhado = await resposta.json();
        alert(`O Java bloqueou! Motivo: ${erroDetalhado.message || erroDetalhado.error || JSON.stringify(erroDetalhado)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão com o servidor!");
    }
  };

  const handleCriarExercicio = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!grupoSelecionadoId) {
      alert("Por favor, selecione um Grupo Muscular!");
      return;
    }

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch("http://localhost:8080/api/exercicios", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({ 
          nome: nomeExercicio,
          descricao: descricaoExercicio,
          grupoMuscularId: Number(grupoSelecionadoId) 
        }),
      });
      
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        setNomeExercicio(""); setDescricaoExercicio(""); setGrupoSelecionadoId("");
        setModalExercicioAberto(false);
        buscarExercicios(); 
      } else {
        const erroDetalhado = await resposta.json();
        alert(`O Java bloqueou! Motivo: ${erroDetalhado.message || erroDetalhado.error || JSON.stringify(erroDetalhado)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão com o servidor!");
    }
  };

  const abrirModalEditar = (ex: Exercicio) => {
    setExercicioEditandoId(ex.id);
    setNomeEdit(ex.nome);
    setDescricaoEdit(ex.descricao || "");
    setModalEditarAberto(true);
  };

  const handleEditarExercicio = async (e: React.FormEvent) => {
    e.preventDefault();
    setSalvando(true);

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();

      const resposta = await fetch(`http://localhost:8080/api/exercicios/${exercicioEditandoId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          nome: nomeEdit,
          descricao: descricaoEdit,
        }),
      });

      if (resposta.ok) {
        setModalEditarAberto(false);
        setExercicioEditandoId(null);
        setNomeEdit("");
        setDescricaoEdit("");
        buscarExercicios();
      } else {
        const erro = await resposta.json();
        alert(`Erro ao editar exercício: ${erro.message || JSON.stringify(erro)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão.");
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Biblioteca de Exercícios</h1>
          <p className="text-zinc-400 mt-1">Gerencie os grupos musculares e os exercícios disponíveis.</p>
        </div>
        <div className="flex gap-3">
          {isAdmin && (
            <button 
              onClick={() => setModalGrupoAberto(true)}
              className="bg-zinc-800 hover:bg-zinc-700 text-zinc-100 px-4 py-2.5 rounded-lg font-medium transition-colors border border-zinc-700"
            >
              + Novo Grupo
            </button>
          )}

          <button 
            onClick={() => setModalExercicioAberto(true)}
            className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-lg shadow-emerald-900/20"
          >
            + Novo Exercício
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Coluna dos Grupos Musculares */}
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl h-fit">
          <div className="bg-zinc-950/50 px-6 py-4 border-b border-zinc-800">
            <h3 className="font-bold text-lg text-zinc-100">Grupos Musculares</h3>
          </div>
          <div className="p-4">
            {grupos.length === 0 ? (
              <p className="text-zinc-500 text-sm text-center py-4">Nenhum grupo cadastrado.</p>
            ) : (
              <ul className="space-y-2">
                {grupos.map(g => {
                  const idReal = g.id || g.grupoMuscularId;
                  return (
                    <li key={idReal} className="px-4 py-3 bg-zinc-950/30 rounded-lg text-zinc-300 border border-zinc-800/50 flex justify-between">
                      <span className="capitalize">{g.nome}</span>
                      <span className="text-zinc-600 text-xs">#{idReal}</span>
                    </li>
                  )
                })}
              </ul>
            )}
          </div>
        </div>

        {/* Coluna dos Exercícios */}
        <div className="lg:col-span-2 bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl h-fit">
          <div className="bg-zinc-950/50 px-6 py-4 border-b border-zinc-800">
            <h3 className="font-bold text-lg text-zinc-100">Exercícios Registrados</h3>
          </div>
          {exercicios.length === 0 ? (
            <div className="p-12 text-center">
              <span className="text-4xl mb-3 block">🏋️‍♂️</span>
              <p className="text-zinc-400">A sua biblioteca está vazia.</p>
              <p className="text-zinc-500 text-sm mt-1">Crie um grupo muscular e depois adicione exercícios.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-zinc-300">
                <thead className="bg-zinc-950/50 text-zinc-500 border-b border-zinc-800">
                  <tr>
                    <th className="px-6 py-4 font-medium">Nome do Exercício</th>
                    <th className="px-6 py-4 font-medium">Descrição</th>
                    <th className="px-6 py-4 font-medium text-right">Ações</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-zinc-800/50">
                  {exercicios.map(ex => (
                    <tr key={ex.id} className="hover:bg-zinc-800/30 transition-colors">
                      <td className="px-6 py-4 font-medium text-zinc-100">{ex.nome}</td>
                      <td className="px-6 py-4 text-zinc-300">{ex.descricao || "-"}</td>
                      <td className="px-6 py-4 text-right">
                        <button
                          onClick={() => abrirModalEditar(ex)}
                          className="text-emerald-500 hover:text-emerald-400 text-xs font-medium bg-emerald-500/10 hover:bg-emerald-500/20 px-3 py-1.5 rounded transition-colors"
                        >
                          Editar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      {/* MODAL: NOVO GRUPO MUSCULAR */}
      {modalGrupoAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-sm shadow-2xl">
            <h2 className="text-xl font-bold text-zinc-100 mb-6">Novo Grupo Muscular</h2>
            <form onSubmit={handleCriarGrupo} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Nome (Ex: Peito, Pernas)</label>
                <input type="text" required value={nomeGrupo} onChange={e => setNomeGrupo(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" />
              </div>
              
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Descrição (Opcional)</label>
                <input type="text" value={descricaoGrupo} onChange={e => setDescricaoGrupo(e.target.value)} placeholder="Ex: Músculos do tórax" className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" />
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={() => setModalGrupoAberto(false)} className="px-4 py-2 text-zinc-400 hover:text-zinc-100 font-medium">Cancelar</button>
                <button type="submit" className="bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2 rounded-lg font-bold">Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: NOVO EXERCÍCIO */}
      {modalExercicioAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-xl font-bold text-zinc-100 mb-6">Novo Exercício</h2>
            <form onSubmit={handleCriarExercicio} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Grupo Muscular</label>
                <select required value={grupoSelecionadoId} onChange={e => setGrupoSelecionadoId(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50">
                  <option value="" disabled>Selecione um grupo...</option>
                  {grupos.map(g => {
                    const idReal = g.id || g.grupoMuscularId;
                    return (
                      <option key={idReal} value={idReal}>{g.nome}</option>
                    )
                  })}
                </select>
                {grupos.length === 0 && <p className="text-xs text-amber-500 mt-1">Crie um Grupo Muscular primeiro!</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Nome do Exercício (Ex: Supino Reto)</label>
                <input type="text" required value={nomeExercicio} onChange={e => setNomeExercicio(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" />
              </div>
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Descrição</label>
                <input type="text" required value={descricaoExercicio} onChange={e => setDescricaoExercicio(e.target.value)} placeholder="Ex: Exercício com barra..." className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" />
              </div>
              <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={() => setModalExercicioAberto(false)} className="px-4 py-2 text-zinc-400 hover:text-zinc-100 font-medium">Cancelar</button>
                <button type="submit" className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2 rounded-lg font-bold shadow-lg">Salvar Exercício</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL: EDITAR EXERCÍCIO */}
      {modalEditarAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-xl font-bold text-zinc-100 mb-6">Editar Exercício</h2>
            <form onSubmit={handleEditarExercicio} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Nome do Exercício</label>
                <input
                  type="text"
                  value={nomeEdit}
                  onChange={(e) => setNomeEdit(e.target.value)}
                  required
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Descrição</label>
                <input
                  type="text"
                  value={descricaoEdit}
                  onChange={(e) => setDescricaoEdit(e.target.value)}
                  placeholder="Ex: Exercício com barra..."
                  className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50"
                />
              </div>
              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setModalEditarAberto(false)}
                  className="px-4 py-2 text-zinc-400 hover:text-zinc-100 font-medium"
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
    </div>
  );
}