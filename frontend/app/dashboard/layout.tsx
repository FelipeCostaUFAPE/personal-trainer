"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";

export default function DashboardLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();

  const handleLogout = () => {
    document.cookie = "token=; path=/; max-age=0";
    router.push("/");
  };

  // FUNÇÃO MÁGICA: Verifica se a URL atual combina com o botão do menu
  const getMenuClass = (caminho: string) => {
    // Exceção pro Início para não bugar os outros links
    if (caminho === "/dashboard" && pathname !== "/dashboard") {
      return "block px-4 py-3 rounded-lg text-zinc-400 hover:bg-zinc-800 hover:text-zinc-100 transition-colors border border-transparent";
    }
    
    // Se a URL contém o caminho do botão, deixa ele aceso (Verde)
    if (pathname.startsWith(caminho)) {
      return "block px-4 py-3 rounded-lg bg-emerald-500/10 text-emerald-500 font-medium transition-colors border border-emerald-500/20";
    }
    
    // Se não, deixa ele cinza normal
    return "block px-4 py-3 rounded-lg text-zinc-400 hover:bg-zinc-800 hover:text-zinc-100 transition-colors border border-transparent";
  };

  return (
    <div className="min-h-screen bg-zinc-950 flex">
      {/* Menu Lateral (Sidebar) */}
      <aside className="w-64 bg-zinc-900 border-r border-zinc-800 flex flex-col fixed h-full z-10">
        
        {/* Logo */}
        <div className="p-6 border-b border-zinc-800">
          <h1 className="text-xl font-bold text-emerald-500 tracking-wider">
            Personal System
          </h1>
        </div>

        {/* Links de Navegação */}
        <nav className="flex-1 p-4 space-y-2">
          <Link href="/dashboard" className={getMenuClass("/dashboard")}>
            🏠 Início
          </Link>
          <Link href="/dashboard/alunos" className={getMenuClass("/dashboard/alunos")}>
            👥 Meus Alunos
          </Link>
          <Link href="/dashboard/exercicios" className={getMenuClass("/dashboard/exercicios")}>
            💪 Exercícios
          </Link>
          <Link href="/dashboard/treinos" className={getMenuClass("/dashboard/treinos")}>
            🏋️‍♂️ Planos de Treino
          </Link>
          <Link href="/dashboard/faturas" className={getMenuClass("/dashboard/faturas")}>
            💳 Faturas
          </Link>
        </nav>

        {/* Rodapé do Menu */}
        <div className="p-4 border-t border-zinc-800">
          <button 
            onClick={handleLogout}
            className="flex items-center gap-3 text-red-400 hover:text-red-300 transition-colors w-full px-4 py-2 rounded-lg hover:bg-red-400/10"
          >
            <span className="w-8 h-8 rounded-full bg-zinc-800 flex items-center justify-center text-zinc-100 text-sm font-bold border border-zinc-700">
              N
            </span>
            Sair do Sistema
          </button>
        </div>
      </aside>

      {/* Conteúdo Principal (Onde as telas abrem) */}
      <main className="flex-1 ml-64 p-8">
        <div className="max-w-7xl mx-auto">
          {children}
        </div>
      </main>
    </div>
  );
}