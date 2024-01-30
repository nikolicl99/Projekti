unit DodajTransakciju;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.Controls.Presentation, FMX.StdCtrls, FMX.Edit, FMX.ListBox,
  FMX.DateTimeCtrls, FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Objects;

type
  Tfrmdodajtransakciju = class(TForm)
    Nazad: TButton;
    Potvrdi: TButton;
    Vrsta_Label: TLabel;
    ZiroRacun_Label: TLabel;
    Vrsta: TComboBox;
    ZiroRacun: TEdit;
    Iznos_Label: TLabel;
    Iznos: TEdit;
    Valuta: TComboBox;
    Valuta_Label: TLabel;
    Datum: TDateEdit;
    Datum_Label: TLabel;
    Nabavka: TComboBox;
    Nabavka_Label: TLabel;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Z: TImage;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure DatumChange(Sender: TObject);
    procedure PotvrdiClick(Sender: TObject);
    procedure NabavkaChange(Sender: TObject);
  private
    { Private declarations }
    SelectedDate: string;
    SelectedNabavkaID: integer;
  public
    { Public declarations }
  end;

var
  frmdodajtransakciju: Tfrmdodajtransakciju;

implementation
uses Transakcije, Main, LoginZaposleni;

{$R *.fmx}



procedure Tfrmdodajtransakciju.DatumChange(Sender: TObject);
begin
SelectedDate:= FormatDateTime('dd/mm/yyyy',Datum.Date);
end;

procedure Tfrmdodajtransakciju.FormCreate(Sender: TObject);
var
  Kveri: TFDQuery;
  Text: String;
begin
  Kveri := TFDQuery.Create(nil);
  try
    Kveri.Connection := GlobalConnection;
    Kveri.SQL.Clear;
    Kveri.SQL.Text := 'SELECT Nabavka.Idnabavke, nabavka.idproizvoda, nabavka.datum_nabavke, nabavka.kolicina, inventar.ime ' +
    'FROM Nabavka '+
    'JOIN Inventar ON Nabavka.idproizvoda = Inventar.id';
    Kveri.Open;

    Nabavka.Clear;

    while not Kveri.Eof do
    begin
      Text := Kveri.FieldByName('IDNabavke').AsString + ' - ' + Kveri.FieldbyName('ime').AsString + ' - ' + Kveri.FieldByName('datum_nabavke').AsString + ' - ' + Kveri.FieldByName('kolicina').AsString;
      Nabavka.Items.AddObject(Text, TObject(Kveri.FieldByName('Idnabavke').AsInteger));
      Kveri.Next;
    end;
  finally
    Kveri.Free;
    Kveri.Close;
  end;
end;

procedure Tfrmdodajtransakciju.NabavkaChange(Sender: TObject);
begin
  if Nabavka.ItemIndex <> -1 then
  begin
    SelectedNabavkaID := Integer(Nabavka.Items.Objects[Nabavka.ItemIndex]);
  end;
end;

procedure Tfrmdodajtransakciju.NazadClick(Sender: TObject);
begin
 frmTransakcije.Show;
 self.Hide;
end;

procedure Tfrmdodajtransakciju.PotvrdiClick(Sender: TObject);
var
MyQuery: TFDQuery;
begin
MyQuery:= TFDQuery.Create(nil);
try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO Transakcije (IDZaposlenog, IDNabavke, Vrsta_Uplate, Ziro_Racun, Iznos, Valuta, Datum_Uplate, Status_Transakcije) VALUES (:IDZaposlenog, :IDNabavke, :Vrsta_Uplate, :Ziro_Racun, :Iznos, :Valuta, :Datum_Uplate, :Status_Transakcije)';
    MyQuery.ParamByName('IDZaposlenog').AsInteger := frmLoginZaposleni.UlogovaniZaposleniID;
    MyQuery.ParamByName('IDNabavke').AsInteger := SelectedNabavkaID;
    MyQuery.ParamByName('Vrsta_Uplate').AsString := Vrsta.Selected.Text;
    MyQuery.ParamByName('Ziro_Racun').AsString := ZiroRacun.Text;
    MyQuery.ParamByName('Iznos').AsString := Iznos.Text;
    MyQuery.ParamByName('Valuta').AsString := Valuta.Selected.Text;
    MyQuery.ParamByName('Datum_Uplate').AsString := SelectedDate;
    MyQuery.ParamByName('Status_Transakcije').AsString := 'WAITING';
    MyQuery.ExecSQL;

  finally
    MyQuery.Free;
    ShowMessage('Transakcije uspesno dodata.');
    frmtransakcije.show;
    self.hide;
  end;
end;

end.
