import { NextRequest, NextResponse } from "next/server";
import axios from "axios";
import { AccessTokenError } from "@auth0/nextjs-auth0/errors";
import { auth0 } from "@/lib/auth0";
import { serverApi } from "@/lib/api";

export async function POST(request: NextRequest) {
  let accessToken: string;

  try {
    const { token } = await auth0.getAccessToken();
    accessToken = token;
  } catch (error) {
    console.error("[api/order] getAccessToken failed:", error);
    if (error instanceof AccessTokenError) {
      return NextResponse.json({ error: "Não autenticado" }, { status: 401 });
    }
    throw error;
  }

  const body = await request.json();

  try {
    const response = await serverApi.post("/api/v1/order", body, {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
    return NextResponse.json(response.data, { status: response.status });
  } catch (error) {
    console.error("[api/order] forwarding to gateway failed:", error);
    if (axios.isAxiosError(error) && error.response) {
      return NextResponse.json(
        error.response.data ?? { error: "Erro ao criar pedido" },
        { status: error.response.status },
      );
    }
    return NextResponse.json({ error: "Erro ao criar pedido" }, { status: 502 });
  }
}
