unit LoginZaposleni;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.StdCtrls,
  FMX.Controls.Presentation, FMX.Edit, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects;

type
  TfrmLogInZaposleni = class(TForm)
    Sifra: TEdit;
    Sifra_Label: TLabel;
    Username: TEdit;
    User_Label: TLabel;
    Prijava: TButton;
    Nazad: TButton;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    procedure NazadClick(Sender: TObject);
    procedure PrijavaClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
    UlogovaniZaposleniID: integer;
  end;

var
  frmLogInZaposleni: TfrmLogInZaposleni;

implementation

{$R *.fmx}
uses Termini_Veterinar, Finansije_GlavnaForma, Inventar, LoginForma, Main, SpisakZaposlenih, CallCentarOdgovorVeterinara, SpisakPromocija;

procedure TfrmLogInZaposleni.NazadClick(Sender: TObject);
begin
frmLogIn.show;
self.hide;
end;

procedure TfrmLogInZaposleni.PrijavaClick(Sender: TObject);
var
  MyQuery: TFDQuery;
  tipZaposlenog: integer;
begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM Zaposleni WHERE User = :Username AND Lozinka = :Sifra';
    MyQuery.Params.ParamByName('Username').AsString := Username.Text;
    MyQuery.Params.ParamByName('Sifra').AsString := Sifra.Text;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
    begin
    UlogovaniZaposleniID := MyQuery.FieldByName('IDZaposlenog').AsInteger;
    tipZaposlenog := MyQuery.FieldByName('Funkcija').AsInteger;
      if tipZaposlenog=1 then
        begin
             frmTerminiVeterinar.Show;
              self.hide;
        end;
        if tipZaposlenog=2 then
        begin
             frmInventar.Show;
              self.hide;
        end;
      if tipZaposlenog=3 then
        begin
             frmPromocije.Show;
              self.hide;
        end;

      if tipZaposlenog=4 then
      begin
          frmZaposleni.show;
          self.hide;
      end;
      if tipZaposlenog=5 then
      begin
          ccform3.show;
          self.hide;
      end;
      if tipZaposlenog=6 then
      begin
          frmfinansijemeni.show;
          self.hide;
      end;
    end
    else
    begin
      ShowMessage('Netačan username ili lozinka.');
    end;
  finally
    MyQuery.Free;
  end;
end;

end.
