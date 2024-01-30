unit StareVakcine;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FMX.DateTimeCtrls, FMX.Objects, FMX.StdCtrls, FMX.ListBox,
  FMX.Controls.Presentation, FMX.Edit, FireDAC.Stan.Intf, FireDAC.Stan.Option,
  FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf,
  FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt, Data.DB,
  FireDAC.Comp.DataSet, FireDAC.Comp.Client;

type
  TfrmStareVakcine = class(TForm)
    Forma: TPanel;
    Nazad: TButton;
    Vakcina_Label: TLabel;
    Revakcinacija_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Vakcine: TEdit;
    Revakcinacija: TEdit;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  frmStareVakcine: TfrmStareVakcine;

implementation

{$R *.fmx}
uses main,StariPregled;

procedure TfrmStareVakcine.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
  'SELECT Klijent_Vakcinacija.*, Vakcine.* ' +
  'FROM Klijent_Vakcinacija ' +
  'JOIN Vakcine ON Klijent_Vakcinacija.vakcina = Vakcine.idvakcine ' +
  'WHERE Klijent_Vakcinacija.pregled = :pregledid';

Query.ParamByName('pregledid').AsInteger := frmstaripregled.pregledid;
Query.Open;
       Vakcine.Text:= Query.FieldByName('imeVakcine').AsString;
       Revakcinacija.Text:= Query.FieldByName('Revakcinacija').AsString;

  finally
    Query.Free;
  end;
end;

procedure TfrmStareVakcine.NazadClick(Sender: TObject);
begin
frmStariPregled.show;
self.hide;
end;

end.
