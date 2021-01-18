export class Allottee {
  id?: number;
  name: string;
  cpf: string;
  email?: string;
  retirement: number;
  retirementValue?: number;
  amountYears: number;
  status?: string;

  static statusAsString(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'Ativo';
      case 'REGISTRATION_PENDING': return 'Pendente de registro';
      case 'EXCLUDED': return 'Excluído';
      case 'REJECTED': return 'Rejeitado';
      default: return 'Não Informado';
    }
  }

  static isActive(status: string): boolean {
    return status === 'ACTIVE';
  }
}

