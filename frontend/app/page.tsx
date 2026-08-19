import { ShowCard } from "@/components/show-card";
import { serverApi } from "@/lib/api";
import { auth0 } from "@/lib/auth0";
import type { Show } from "@/lib/types";

export const dynamic = "force-dynamic";

async function getShows(accessToken: string): Promise<{ shows: Show[]; error: boolean }> {
  try {
    const response = await serverApi.get<Show[]>("/api/v1/show/all", {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    return { shows: response.data, error: false };
  } catch {
    return { shows: [], error: true };
  }
}

export default async function Home() {
  const session = await auth0.getSession();

  if (!session) {
    return (
      <>
        {/* Redirects to Auth0 to sign up */}
        <a href="/auth/login?screen_hint=signup">Signup</a>
        <br />
        {/* Redirects to Auth0 to log in */}
        <a href="/auth/login">Login</a>
      </>
    );
  }

  const { token } = await auth0.getAccessToken();
  const { shows, error } = await getShows(token);

  return (
    <main className="mx-auto max-w-6xl px-6 py-12">
      <h1 className="text-3xl font-bold tracking-tight">micro-ticket</h1>
      <h1>Ola, {session.user.name}!</h1>
      {/* Redirects to Auth0 to log out and clears the local session */}
      <a href="/auth/logout">Logout</a>
      <p className="mt-1 text-muted-foreground">Shows disponíveis para compra</p>

      {error && (
        <p className="mt-8 rounded-lg border border-dashed p-6 text-sm text-muted-foreground">
          Não foi possível carregar os shows agora. Tente novamente em instantes.
        </p>
      )}

      {!error && shows.length === 0 && (
        <p className="mt-8 rounded-lg border border-dashed p-6 text-sm text-muted-foreground">
          Nenhum show disponível no momento.
        </p>
      )}

      {shows.length > 0 && (
        <div className="mt-8 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {shows.map((show) => (
            <ShowCard key={show.id} show={show} />
          ))}
        </div>
      )}
    </main>
  );
}
