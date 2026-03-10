"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

// Moldes de Dados
interface Aluno {
  id: number;
  nome: string;
}

interface Fatura {
  id: number;
  valor: number;
  dataVencimento: string;
  status: string; 
  alunoId?: number;
  aluno?: Aluno;
}

export default function FaturasPage() {
  const router = useRouter();
  
  const [faturas, setFaturas] = useState<Fatura[]>([]);
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [carregando, setCarregando] = useState(true);

  // Estados do Modal
  const [modalNovaFaturaAberto, setModalNovaFaturaAberto] = useState(false);
  const [salvandoFatura, setSalvandoFatura] = useState(false);

  // Campos do Formulário
  const [alunoSelecionadoId, setAlunoSelecionadoId] = useState("");
  const [valor, setValor] = useState("");
  const [dataVencimento, setDataVencimento] = useState("");

  const lidarComErro401 = (status: number) => {
    if (status === 401) {
      document.cookie = "token=; path=/; max-age=0";
      alert("Sua sessão expirou por segurança. Faça login novamente.");
      router.push("/");
      return true;
    }
    return false;
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    setCarregando(true);
    await buscarAlunos();
    await buscarFaturas();
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
        console.log("Rota alunos admin falhou (esperado para personal), tentando me/alunos...");
        resposta = await fetch("http://localhost:8080/api/personais/me/alunos", {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      if (resposta.ok) {
        setAlunos(await resposta.json());
      } else {
        // Só loga erro se AMBAS falharem (não alerta o usuário)
        console.error("Falha ao buscar alunos após fallback. Status:", resposta.status);
      }
    } catch (error) {
      console.error("Erro de rede ao buscar alunos:", error);
    }
  };

  const buscarFaturas = async () => {
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1]?.trim();
      if (!token) return;

      let resposta = await fetch("http://localhost:8080/api/faturas", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (resposta.status === 403 || resposta.status === 401 || resposta.status === 500) {
        console.log("Rota faturas admin falhou (esperado para personal), tentando /me...");
        resposta = await fetch("http://localhost:8080/api/faturas/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      if (resposta.ok) {
        setFaturas(await resposta.json());
      } else {
        // Só loga erro se AMBAS falharem (não alerta o usuário)
        console.error("Falha ao buscar faturas após fallback. Status:", resposta.status);
      }
    } catch (error) {
      console.error("Erro de rede ao buscar faturas:", error);
    }
  };

  const handleCriarFatura = async (e: React.FormEvent) => {
    e.preventDefault();
    setSalvandoFatura(true);

    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      
      const formatarData = (dataOriginal: string) => {
        const [ano, mes, dia] = dataOriginal.split("-");
        return `${dia}/${mes}/${ano}`;
      };

      const resposta = await fetch("http://localhost:8080/api/faturas", {
        method: "POST",
        headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
        body: JSON.stringify({ 
          alunoId: Number(alunoSelecionadoId),
          valor: Number(valor),
          dataVencimento: formatarData(dataVencimento)
        }),
      });
      
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        setAlunoSelecionadoId(""); setValor(""); setDataVencimento("");
        setModalNovaFaturaAberto(false);
        buscarFaturas(); 
      } else {
        const erroDetalhado = await resposta.json();
        alert(`O Java bloqueou! Motivo: ${erroDetalhado.message || erroDetalhado.error || JSON.stringify(erroDetalhado)}`);
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão!");
    } finally {
      setSalvandoFatura(false);
    }
  };

  const handlePagarFatura = async (id: number) => {
    if (!confirm("Confirmar o pagamento desta fatura?")) return;
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch(`http://localhost:8080/api/faturas/${id}/pagar`, {
        method: "PATCH",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) buscarFaturas(); 
    } catch (error) {
      console.error(error);
    }
  };

  const handleCancelarFatura = async (id: number) => {
    if (!confirm("Tem certeza que deseja cancelar esta cobrança?")) return;
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch(`http://localhost:8080/api/faturas/${id}/cancelar`, {
        method: "PATCH",
        headers: { Authorization: `Bearer ${token}` },
      });
      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) buscarFaturas(); 
    } catch (error) {
      console.error(error);
    }
  };

  // FUNÇÃO NOVA: A FAXINA DA FATURA 🧹
  const handleExcluirFatura = async (id: number) => {
    if (!confirm("⚠️ ATENÇÃO: Tem certeza que deseja APAGAR DEFINITIVAMENTE esta fatura do sistema?")) return;
    
    try {
      const token = document.cookie.split("; ").find(row => row.startsWith("token="))?.split("=")[1];
      const resposta = await fetch(`http://localhost:8080/api/faturas/${id}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (lidarComErro401(resposta.status)) return;
      if (resposta.ok) {
        buscarFaturas(); // Atualiza a tabela fazendo a fatura sumir!
      } else {
        alert("Erro ao tentar excluir a fatura do banco de dados.");
      }
    } catch (error) {
      console.error(error);
      alert("Erro de conexão ao tentar excluir.");
    }
  };

  const getStatusCor = (status: string) => {
    if (!status) return "bg-zinc-500/10 text-zinc-500 border-zinc-500/20";
    if (status.toUpperCase() === "PAGA") return "bg-emerald-500/10 text-emerald-500 border-emerald-500/20";
    if (status.toUpperCase() === "PENDENTE") return "bg-amber-500/10 text-amber-500 border-amber-500/20";
    if (status.toUpperCase() === "CANCELADA") return "bg-red-500/10 text-red-500 border-red-500/20";
    return "bg-zinc-500/10 text-zinc-500 border-zinc-500/20";
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-zinc-100">Gestão Financeira</h1>
          <p className="text-zinc-400 mt-1">Gerencie as mensalidades e faturas dos seus alunos.</p>
        </div>
        <button 
          onClick={() => setModalNovaFaturaAberto(true)}
          className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-medium transition-colors shadow-lg shadow-emerald-900/20"
        >
          + Nova Cobrança
        </button>
      </div>

      {carregando ? (
        <div className="p-12 text-center text-zinc-400">Carregando painel financeiro...</div>
      ) : (
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden shadow-xl">
          {faturas.length === 0 ? (
            <div className="p-12 text-center">
               <span className="text-4xl mb-3 block">💸</span>
               <p className="text-zinc-400">Nenhuma fatura cadastrada no sistema.</p>
               <p className="text-zinc-500 text-sm mt-1">Clique no botão verde acima para gerar a primeira mensalidade.</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-zinc-300">
                <thead className="bg-zinc-950/50 text-zinc-500 border-b border-zinc-800">
                  <tr>
                    <th className="px-6 py-4 font-medium">ID</th>
                    <th className="px-6 py-4 font-medium">Aluno</th>
                    <th className="px-6 py-4 font-medium">Vencimento</th>
                    <th className="px-6 py-4 font-medium">Valor</th>
                    <th className="px-6 py-4 font-medium">Status</th>
                    <th className="px-6 py-4 font-medium text-right">Ações</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-zinc-800/50">
                  {faturas.map(fat => {
                    const alunoEncontrado = fat.aluno?.nome || alunos.find(a => a.id === fat.alunoId)?.nome || "Aluno Desconhecido";
                    const estaPendente = fat.status?.toUpperCase() === "PENDENTE";

                    return (
                      <tr key={fat.id} className="hover:bg-zinc-800/30 transition-colors">
                        <td className="px-6 py-4 font-medium text-zinc-500">#{fat.id}</td>
                        <td className="px-6 py-4 font-medium text-zinc-100">{alunoEncontrado}</td>
                        <td className="px-6 py-4">{fat.dataVencimento}</td>
                        <td className="px-6 py-4 font-medium text-zinc-100">
                          R$ {fat.valor?.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                        </td>
                        <td className="px-6 py-4">
                          <span className={`px-3 py-1 text-xs font-bold rounded-full border ${getStatusCor(fat.status)}`}>
                            {fat.status || "DESCONHECIDO"}
                          </span>
                        </td>
                        <td className="px-6 py-4 text-right flex justify-end gap-2">
                          
                          {/* Botões de Mudar Status (Só aparecem se for Pendente) */}
                          {estaPendente && (
                            <>
                              <button onClick={() => handlePagarFatura(fat.id)} className="text-emerald-500 hover:text-emerald-400 text-xs font-medium bg-emerald-500/10 hover:bg-emerald-500/20 px-3 py-1.5 rounded transition-colors">
                                Dar Baixa
                              </button>
                              <button onClick={() => handleCancelarFatura(fat.id)} className="text-amber-500 hover:text-amber-400 text-xs font-medium bg-amber-500/10 hover:bg-amber-500/20 px-3 py-1.5 rounded transition-colors">
                                Cancelar
                              </button>
                            </>
                          )}

                          {/* Botão Fixo de Apagar (Aparece Sempre) */}
                          <button 
                            onClick={() => handleExcluirFatura(fat.id)}
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
            </div>
          )}
        </div>
      )}

      {/* ========== MODAL: NOVA FATURA ========== */}
      {modalNovaFaturaAberto && (
        <div className="fixed inset-0 bg-black/80 flex items-center justify-center z-50">
          <div className="bg-zinc-900 border border-zinc-800 p-8 rounded-2xl w-full max-w-md shadow-2xl">
            <h2 className="text-2xl font-bold text-zinc-100 mb-6">Nova Cobrança</h2>
            <form onSubmit={handleCriarFatura} className="space-y-4">
              
              <div>
                <label className="block text-sm font-medium text-zinc-300 mb-2">Aluno</label>
                <select required value={alunoSelecionadoId} onChange={e => setAlunoSelecionadoId(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50">
                  <option value="" disabled>Selecione um aluno...</option>
                  {alunos.map(a => (
                    <option key={a.id} value={a.id}>{a.nome}</option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-zinc-300 mb-2">Valor (R$)</label>
                  <input type="number" step="0.01" min="0" required value={valor} onChange={e => setValor(e.target.value)} placeholder="Ex: 150.00" className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-zinc-300 mb-2">Vencimento</label>
                  <input type="date" required value={dataVencimento} onChange={e => setDataVencimento(e.target.value)} className="w-full px-4 py-3 rounded-lg bg-zinc-950 border border-zinc-800 text-zinc-100 focus:outline-none focus:ring-2 focus:ring-emerald-500/50" />
                </div>
              </div>

              <div className="flex justify-end gap-3 pt-4">
                <button type="button" onClick={() => setModalNovaFaturaAberto(false)} className="px-4 py-2.5 text-zinc-400 hover:text-zinc-100 font-medium">Cancelar</button>
                <button type="submit" disabled={salvandoFatura} className="bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-bold shadow-lg disabled:opacity-50">
                  {salvandoFatura ? "Gerando..." : "Gerar Fatura"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}