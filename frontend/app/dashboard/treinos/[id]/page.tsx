"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";

interface ItemTreino {
  id: number;
  series: number;
  repeticoes: number;
  cargaKg?: number; 
  carga?: number;
  descansoSegundos?: number;
  descanso?: number;
  exercicioId?: number; 
  exercicio?: { id: number; nome: string };
}

interface DiaTreino {
  id: number;
  diaSemana: string;
  itensTreino?: ItemTreino[]; 
  itens?: ItemTreino[];       
}

interface PlanoCompleto {
  id: number;
  alunoId: number; 
  nome: string;
  dataFim: string;
  dias: DiaTreino[]; 
}

interface ExercicioOpcao {
  id?: number;
  grupoMuscularId?: number; // Para o caso de o Java mandar este nome
  nome: string;
}

export default function DetalhesTreino() {
  const params = useParams(); 
  const router = useRouter();
  
  const [plano, setPlano] = useState<PlanoCompleto | null>(null);
  const [carregando, setCarregando] = useState(true);

  // Modal Dia
  const [modalDiaAberto, setModalDiaAberto] = useState(false);
  const [diaSemana, setDiaSemana] = useState("SEGUNDA");
  const [salvandoDia, setSalvandoDia] = useState(false);

  // Modal Exercício
  const [modalExercicioAberto, setModalExercicioAberto] = useState(false);
  const [diaSelecionadoId, setDiaSelecionadoId] = useState<number | null>(null);
  const [exerciciosDb, setExerciciosDb] = useState<ExercicioOpcao[]>([]);
  const [salvandoExercicio, setSalvandoExercicio] = useState(false);

  // Campos Exercício
  const [exercicioId, setExercicioId] = useState("");
  const [series, setSeries] = useState("3");
  const [repeticoes, setRepeticoes] = useState("12");
  const [carga, setCarga] = useState("0");
  const [descanso, setDescanso] = useState("60");

  const planoId = params.id;

  const lidarComErro401 = (status: number) => {
    if (status === 401) {
      document.cookie = "token=; path=/; max-age=0";
      alert("A sua sessão expirou por segurança. Faça login novamente.");
      router.push("/");
      return true;
    }
    return false;
  };

  useEffect(() => {
    if (planoId) {
      buscarPlanoCompleto();
      buscarExerciciosDoBanco();
    }
  }, [planoId]);

  const buscarPlanoCompleto = async () => {
    try {
      const token = document.cookie.split("; ").find((row) => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch(`http://localhost:8080/api/planos/${planoId}/completo`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        setPlano(await resposta.json());
      }
    } catch (error) {
      console.error(error);
    } finally {
      setCarregando(false);
    }
  };

  const buscarExerciciosDoBanco = async () => {
    try {
      const token = document.cookie.split("; ").find((row) => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch("http://localhost:8080/api/exercicios", {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (resposta.ok) setExerciciosDb(await resposta.json());
    } catch (error) {
      console.error(error);
    }
  };

  const handleCriarDia = async (e: React.FormEvent) => {
    e.preventDefault();
    setSalvandoDia(true);
    try {
      const token = document.cookie.split("; ").find((row) => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch(`http://localhost:8080/api/dias/plano/${planoId}`, {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({ diaSemana }),
      });
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        setModalDiaAberto(false);
        buscarPlanoCompleto();
      } else {
        const erro = await resposta.json();
        alert(`Erro: ${erro.message || JSON.stringify(erro)}`);
      }
    } catch (error) {
      console.error(error);
    } finally {
      setSalvandoDia(false);
    }
  };

  const handleCriarExercicio = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!exercicioId) return alert("Selecione um exercício da lista.");

    setSalvandoExercicio(true);
    try {
      const token = document.cookie.split("; ").find((row) => row.startsWith("token="))?.split("=")[1];
      
      const resposta = await fetch(`http://localhost:8080/api/itens/dia/${diaSelecionadoId}/itens`, {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({
          exercicioId: Number(exercicioId),
          series: Number(series),
          repeticoes: Number(repeticoes),
          cargaKg: Number(carga), // 1. O NOME CORRETO DA CARGA!
          descansoSegundos: Number(descanso), 
        }),
      });

      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        setModalExercicioAberto(false);
        buscarPlanoCompleto(); 
        setExercicioId(""); setSeries("3"); setRepeticoes("12"); setCarga("0"); setDescanso("60");
      } else {
        const erroDetalhado = await resposta.json();
        alert(`O Java bloqueou! Motivo: ${erroDetalhado.message || erroDetalhado.error || JSON.stringify(erroDetalhado)}`);
      }
    } catch (error) {
      console.error(error);
    } finally {
      setSalvandoExercicio(false);
    }
  };

  // FUNÇÃO NOVA: A FAXINA! 🧹
  const handleExcluirExercicio = async (itemId: number) => {
    if (!confirm("Tem certeza que deseja apagar este exercício da ficha?")) return;
    
    try {
      const token = document.cookie.split("; ").find((row) => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch(`http://localhost:8080/api/itens/${itemId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        buscarPlanoCompleto(); // atualiza a tabela fazendo o item sumir
      } else {
        alert("Erro ao tentar excluir o exercício do banco de dados.");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão ao tentar excluir.");
    }
  };

  const abrirModalExercicio = (idDoDia: number) => {
    setDiaSelecionadoId(idDoDia);
    setModalExercicioAberto(true);
  };

  if (carregando) return <div className="p-10 text-center text-zinc-400">Carregando ficha...</div>;
  if (!plano) return <div className="p-10 text-center text-red-400">Plano não encontrado!</div>;

  return (
    <div className="max-w-5xl mx-auto pb-10">
      <div className="flex items-center gap-4 mb-8 border-b border-zinc-800 pb-6">
        <Link href="/dashboard/treinos" className="text-zinc-500 hover:text-zinc-300 transition-colors text-2xl">←</Link>
        <div>
          <h1 className="text-3xl font-bold text-emerald-500">{plano.nome}</h1>
          <p className="text-zinc-400 mt-1">ID do Aluno(a): <span className="text-zinc-100 font-medium">#{plano.alunoId}</span> | Válido até: {plano.dataFim}</p>
        </div>
      </div>

      <div className="flex gap-4 mb-8">
        <button onClick={() => setModalDiaAberto(true)} className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-lg shadow-emerald-900/20">
          + Adicionar Dia (Ex: Segunda-feira)
        </button>
      </div>

      {(!plano.dias || plano.dias.length === 0) ? (
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-12 text-center shadow-xl">
          <span className="text-5xl mb-4 block">👻</span>
          <p className="text-zinc-300 font-medium text-xl">Esta ficha ainda está vazia!</p>
        </div>
      ) : (
        <div className="space-y-6">
          {plano.dias.map((dia) => {
            const listaExercicios = dia.itens || dia.itensTreino || [];

            return (
              <div key={dia.id} className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl">
                <div className="bg-zinc-950/50 px-6 py-4 border-b border-zinc-800 flex justify-between items-center">
                  <h3 className="font-bold text-lg text-zinc-100 capitalize">{dia.diaSemana}</h3>
                  <button onClick={() => abrirModalExercicio(dia.id)} className="text-sm text-emerald-500 hover:text-emerald-400 font-medium bg-emerald-500/10 px-3 py-1.5 rounded-md">
                    + Adicionar Exercício
                  </button>
                </div>
                
                <div className="p-6">
                  {listaExercicios.length === 0 ? (
                    <p className="text-zinc-500 text-sm text-center py-4">Nenhum exercício cadastrado neste dia.</p>
                  ) : (
                    <table className="w-full text-left text-sm text-zinc-300">
                      <thead>
                        <tr className="text-zinc-500 border-b border-zinc-800/50">
                          <th className="pb-3 font-medium">Exercício</th>
                          <th className="pb-3 font-medium">Séries x Rep</th>
                          <th className="pb-3 font-medium">Carga</th>
                          <th className="pb-3 font-medium">Descanso</th>
                          <th className="pb-3 font-medium text-right">Ação</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-zinc-800/50">
                        {listaExercicios.map((item) => {
                          
                          // 2. A MÁGICA DE ACHAR O NOME: 
                          // Ele procura na biblioteca o ID que o Java enviou
                          const exercicioEncontrado = exerciciosDb.find(ex => (ex.id === item.exercicioId || ex.grupoMuscularId === item.exercicioId));
                          const nomeExato = exercicioEncontrado ? exercicioEncontrado.nome : (item.exercicio?.nome || "Exercício Desconhecido");

                          return (
                            <tr key={item.id} className="hover:bg-zinc-800/30 transition-colors">
                              <td className="py-3 font-medium text-zinc-100">{nomeExato}</td>
                              <td className="py-3">{item.series} x {item.repeticoes}</td>
                              <td className="py-3">{item.cargaKg || item.carga || 0} kg</td>
                              <td className="py-3">{item.descansoSegundos || item.descanso || 0}s</td>
                              <td className="py-3 text-right">
                                <button 
                                  onClick={() => handleExcluirExercicio(item.id)} 
                                  className="text-red-500 hover:text-red-400 text-xs font-medium bg-red-500/10 hover:bg-red-500/20 px-3 py-1.5 rounded transition-colors"
                                >
                                  Apagar
                                </button>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* MODAL DIA */}
      {modalDiaAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-2xl font-bold text-zinc-100 mb-6">Novo Dia de Treino</h2>
            <form onSubmit={handleCriarDia} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Selecione o Dia</label>
                <select value={diaSemana} onChange={(e) => setDiaSemana(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100">
                  <option value="SEGUNDA">Segunda-feira</option>
                  <option value="TERCA">Terça-feira</option>
                  <option value="QUARTA">Quarta-feira</option>
                  <option value="QUINTA">Quinta-feira</option>
                  <option value="SEXTA">Sexta-feira</option>
                  <option value="SABADO">Sábado</option>
                  <option value="DOMINGO">Domingo</option>
                </select>
              </div>
              <div className="flex justify-end gap-3 pt-2">
                <button type="button" onClick={() => setModalDiaAberto(false)} className="px-5 py-2.5 text-zinc-400 hover:text-zinc-100 font-medium">Cancelar</button>
                <button type="submit" disabled={salvandoDia} className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium shadow-lg disabled:opacity-50">{salvandoDia ? "Salvando..." : "Salvar"}</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* MODAL EXERCÍCIO */}
      {modalExercicioAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-xl shadow-2xl">
            <h2 className="text-2xl font-bold text-zinc-100 mb-6">Adicionar Exercício</h2>
            <form onSubmit={handleCriarExercicio} className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Exercício</label>
                <select value={exercicioId} onChange={(e) => setExercicioId(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100">
                  <option value="" disabled>Selecione um exercício...</option>
                  {exerciciosDb.map((ex) => {
                    const idReal = ex.id || ex.grupoMuscularId;
                    return <option key={idReal} value={idReal}>{ex.nome}</option>
                  })}
                </select>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div>
                  <label className="text-sm font-medium text-zinc-300 mb-2">Séries</label>
                  <input type="number" min="1" value={series} onChange={(e) => setSeries(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100" />
                </div>
                <div>
                  <label className="text-sm font-medium text-zinc-300 mb-2">Repetições</label>
                  <input type="number" min="1" value={repeticoes} onChange={(e) => setRepeticoes(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100" />
                </div>
                <div>
                  <label className="text-sm font-medium text-zinc-300 mb-2">Carga (kg)</label>
                  <input type="number" min="0" value={carga} onChange={(e) => setCarga(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100" />
                </div>
                <div>
                  <label className="text-sm font-medium text-zinc-300 mb-2">Descanso (s)</label>
                  <input type="number" min="0" step="5" value={descanso} onChange={(e) => setDescanso(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100" />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={() => setModalExercicioAberto(false)} className="px-5 py-2.5 text-zinc-400 hover:text-zinc-100 font-medium">Cancelar</button>
                <button type="submit" disabled={salvandoExercicio} className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium shadow-lg disabled:opacity-50">{salvandoExercicio ? "Salvando..." : "Adicionar à Ficha"}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}