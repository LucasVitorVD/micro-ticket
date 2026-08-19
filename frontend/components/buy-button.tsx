"use client";

import { useState } from "react";
import axios from "axios";
import { Button } from "@/components/ui/button";

type PurchaseState = "idle" | "loading" | "success" | "error";

export function BuyButton({ showId, soldOut }: { showId: string; soldOut: boolean }) {
  const [state, setState] = useState<PurchaseState>("idle");

  async function handleBuy() {
    setState("loading");
    try {
      await axios.post("/api/order", { showId, quantity: 1 });
      setState("success");
    } catch {
      setState("error");
    }
  }

  if (soldOut) {
    return (
      <Button disabled variant="outline" className="w-full">
        Esgotado
      </Button>
    );
  }

  const label = {
    idle: "Comprar",
    loading: "Comprando...",
    success: "Comprado!",
    error: "Erro, tentar novamente",
  }[state];

  return (
    <Button
      onClick={handleBuy}
      disabled={state === "loading" || state === "success"}
      variant={state === "error" ? "destructive" : "default"}
      className="w-full"
    >
      {label}
    </Button>
  );
}
