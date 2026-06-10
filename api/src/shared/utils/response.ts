export interface PaginationMeta {
  page: number;
  limit: number;
  total: number;
  totalPages: number;
}

export const response = {
  success<T>(data: T, message = "Operação realizada com sucesso.") {
    return { success: true, message, data };
  },

  paginated<T>(data: T[], meta: PaginationMeta) {
    return { success: true, data, meta };
  },

  error(message: string, details?: unknown, code?: string) {
    return {
      success: false,
      message,
      ...(details !== undefined && { details }),
      ...(code !== undefined && { code }),
    };
  },
};
